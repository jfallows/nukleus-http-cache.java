/**
 * Copyright 2016-2019 The Reaktivity Project
 *
 * The Reaktivity Project licenses this file to you under the Apache License,
 * version 2.0 (the "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations
 * under the License.
 */
package org.reaktivity.nukleus.http_cache.internal.stream;

final class Signals
{
    public static final int CACHE_ENTRY_UPDATED_SIGNAL = 1;
    public static final int CACHE_ENTRY_ABORTED_SIGNAL = 3;
    public static final int REQUEST_EXPIRED_SIGNAL = 4;
    public static final int REQUEST_GROUP_LEADER_UPDATED_SIGNAL = 5;
    public static final int CACHE_ENTRY_INVALIDATED_SIGNAL = 6;
    public static final int GROUP_REQUEST_RETRY_SIGNAL = 7;
    public static final int CACHE_ENTRY_NOT_MODIFIED_SIGNAL = 8;
    public static final int CACHE_ENTRY_READY_SIGNAL = 9;
    public static final int REQUEST_ABORTED_SIGNAL = 10;

    private Signals()
    {
        // utility
    }
}
