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

trait Visitor[A, T] {
  def onNull: T
  def onBoolean(value: Boolean): T
  def onNumber(value: YamlNumber): T
  def onString(value: String): T
  def onArray(value: Iterable[A]): T
  def onObject(value: Iterable[(String, A)]): T
}

trait Visitable[T] {
  def visit[O](t: T, visitor: Visitor[T, O]): O
}