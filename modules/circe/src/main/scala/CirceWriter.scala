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

import io.circe.Json

package object circe {
  implicit object CirceWriter extends Writer[Json] {

    override def ynull: Json = Json.Null

    override def yfalse: Json = Json.False

    override def ytrue: Json = Json.True

    override def ybool(b: Boolean): Json = Json.fromBoolean(b)

    override def ydouble(d: Double): Json = Json.fromDoubleOrNull(d)

    override def ylong(l: Long): Json = Json.fromLong(l)

    override def yint(i: Int): Json = Json.fromInt(i)

    override def ybigdecimal(i: BigDecimal): Json = Json.fromBigDecimal(i)

    override def ystring(s: String): Json = Json.fromString(s)

    override def yarray(vs: Iterable[Json]): Json = Json.fromValues(vs)

    override def yobject(vs: Iterable[(String, Json)]): Json =
      Json.fromFields(vs)

  }
}
