/*
 * Derived from https://github.com/spray/spray/blob/v1.1-M7/spray-http/src/main/scala/spray/http/parser/AcceptRangesHeader.scala
 *
 * Copyright (C) 2011-2012 spray.io
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.http4s
package parser

import org.parboiled2._
import org.http4s.Header.`Accept-Ranges`

private[parser] trait AcceptRangesHeader {

  def ACCEPT_RANGES(input: String) = new AcceptRangesParser(input).parse

  private class AcceptRangesParser(input: ParserInput) extends Http4sHeaderParser[`Accept-Ranges`](input) {

    def entry: Rule1[`Accept-Ranges`] = rule {
      RangeUnitsDef ~ EOL ~> (Header.`Accept-Ranges`(_: Seq[RangeUnit]))
    }

    def RangeUnitsDef: Rule1[Seq[RangeUnit]] = rule {
      NoRangeUnitsDef | zeroOrMore(RangeUnit).separatedBy(ListSep)
    }

    def NoRangeUnitsDef: Rule1[Seq[RangeUnit]] = rule {
      "none" ~ push(List.empty[RangeUnit])
    }

    /* 3.12 Range Units http://www.w3.org/Protocols/rfc2616/rfc2616-sec3.html */

    def RangeUnit: Rule1[RangeUnit] = rule { BytesUnit | OtherRangeUnit }

    def BytesUnit: Rule1[RangeUnit] = rule { "bytes" ~ push(org.http4s.RangeUnit.bytes) }

    def OtherRangeUnit: Rule1[RangeUnit] = rule { Token ~> org.http4s.RangeUnit.CustomRangeUnit }
  }
}