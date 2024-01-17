/*******************************************************************************
 *     ___                  _   ____  ____
 *    / _ \ _   _  ___  ___| |_|  _ \| __ )
 *   | | | | | | |/ _ \/ __| __| | | |  _ \
 *   | |_| | |_| |  __/\__ \ |_| |_| | |_) |
 *    \__\_\\__,_|\___||___/\__|____/|____/
 *
 *  Copyright (c) 2014-2019 Appsicle
 *  Copyright (c) 2019-2023 QuestDB
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 ******************************************************************************/

package io.questdb.test.std.fastdouble;

import io.questdb.std.MemoryTag;
import io.questdb.std.NumericException;
import io.questdb.std.Unsafe;
import io.questdb.std.fastdouble.FastFloatParser;
import io.questdb.std.str.Utf8s;

import static org.junit.Assert.assertEquals;

public class FastFloatParserFromMemLexicallyGeneratedTest extends AbstractLexicallyGeneratedTest {

    @Override
    protected void testAgainstJdk(String str) {
        int len = str.length();
        long mem = Unsafe.malloc(len, MemoryTag.NATIVE_DEFAULT);
        try {
            Utf8s.strCpyAscii(str, mem);
            float expected = 0.0f;
            boolean isExpectedToFail = false;
            try {
                expected = Float.parseFloat(str);
            } catch (NumberFormatException t) {
                isExpectedToFail = true;
            }

            float actual = 0;
            boolean actualFailed = false;
            try {
                actual = FastFloatParser.parseFloat(mem, len, false);
            } catch (NumericException t) {
                actualFailed = true;
            }

            assertEquals(isExpectedToFail, actualFailed);
            if (!isExpectedToFail) {
                assertEquals("str=" + str, expected, actual, 0.001);
                assertEquals("intBits of " + expected,
                        Float.floatToIntBits(expected),
                        Float.floatToIntBits(actual)
                );
            }
        } finally {
            Unsafe.free(mem, len, MemoryTag.NATIVE_DEFAULT);
        }
    }
}
