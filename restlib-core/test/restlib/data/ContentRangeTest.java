/*
 * Copyright (C) 2012 David Bordoley
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


package restlib.data;

import java.util.List;

import restlib.test.AbstractValueObjectTest;

import com.google.common.collect.ImmutableList;

public final class ContentRangeTest extends AbstractValueObjectTest<ContentRange> {
    @Override
    protected List<ContentRange> newTestSet() {
        return ImmutableList.of(
                ContentRange.NONE,
                ContentRange.byteRange(0, 10, 100),
                ContentRange.byteRange(0, 20, 100),
                ContentRange.byteRange(0, 20, 200));
    }
}
