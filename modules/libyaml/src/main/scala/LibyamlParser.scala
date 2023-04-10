/*
 * Copyright 2023 Hossein Naderi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.hnaderi.libyaml

import scala.scalanative.unsafe.*
import scala.scalanative.unsigned.*

import all.*
import scala.collection.mutable
import scala.annotation.tailrec

object LibyamlParser extends Parser {

  override def parse[T: Writer](input: String): Either[Throwable, T] = Zone {
    implicit zone =>
      val input_c = toCString(input).asInstanceOf[Ptr[CUnsignedChar]]

      val parser = yaml_parser_t()

      for {
        _ <- handle(yaml_parser_initialize(parser), ParserInitFailed)

        _ = yaml_parser_set_input_string(parser, input_c, input.size.toULong)

        document = yaml_document_t()
        _ <- handle(yaml_parser_load(parser, document), DocumentLoadFailed)

        doc = LibYamlDocument(document)

        result <- doc.visit[T].toRight(NoDocument)
      } yield {
        doc.clean
        yaml_parser_delete(parser)
        result
      }
  }

  private def handle(result: CInt, err: => Throwable) =
    Either.cond(result != 0, (), err)

  override def parseDocuments[T: Writer](
      input: String
  ): Either[Throwable, Iterable[T]] = Zone { implicit zone =>
    val input_c = toCString(input).asInstanceOf[Ptr[CUnsignedChar]]

    val parser = yaml_parser_t()
    val result = mutable.ListBuffer.empty[T]

    handle(yaml_parser_initialize(parser), ParserInitFailed).flatMap(_ =>

      yaml_parser_set_input_string(parser, input_c, input.size.toULong)
      val document = yaml_document_t()
      val doc = LibYamlDocument(document)

      @tailrec
      def go(): Either[Throwable, Iterable[T]] =
        if (yaml_parser_load(parser, document) == 0)
        then Left(DocumentLoadFailed)
        else {
          val t = doc.visit[T]
          doc.clean

          t match {
            case None => Right(result)
            case Some(value) =>
              result.addOne(value)
              go()
          }
        }

      val out = go()
      yaml_parser_delete(parser)
      out
    )

  }

  case object ParserInitFailed extends Exception("Failed to initialize parser!")
  case object DocumentLoadFailed extends Exception("Failed to load document!")
  case object NoDocument
      extends Exception("Expected at least one document, but found none!")

}
