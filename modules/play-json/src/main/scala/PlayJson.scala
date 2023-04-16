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

package dev.hnaderi.yaml4s

import play.api.libs.json._

package object playjson {
  implicit object PlayJsonWriter extends Writer[JsValue] {
    override def ynull: JsValue = JsNull
    override def yfalse: JsValue = JsFalse
    override def ytrue: JsValue = JsTrue
    override def ybool(b: Boolean): JsValue = JsBoolean(b)
    override def ydouble(d: Double): JsValue = JsNumber(d)
    override def ylong(l: Long): JsValue = JsNumber(l)
    override def yint(i: Int): JsValue = JsNumber(i)
    override def ybigdecimal(i: BigDecimal): JsValue = JsNumber(i)
    override def ystring(s: String): JsValue = JsString(s)
    override def yarray(vs: Iterable[JsValue]): JsValue = JsArray(
      vs.toIndexedSeq
    )
    override def yobject(vs: Iterable[(String, JsValue)]): JsValue = JsObject(
      vs.toSeq
    )
  }

  implicit object PlayJsonVisitable extends Visitable[JsValue] {
    override def visit[O](t: JsValue, visitor: Visitor[JsValue, O]): O =
      t match {
        case JsNumber(value)      => visitor.onNumber(YamlNumber(value))
        case JsObject(underlying) => visitor.onObject(underlying)
        case JsString(value)      => visitor.onString(value)
        case JsNull               => visitor.onNull
        case JsArray(value)       => visitor.onArray(value)
        case JsFalse              => visitor.onBoolean(false)
        case JsTrue               => visitor.onBoolean(true)
      }
  }
}
