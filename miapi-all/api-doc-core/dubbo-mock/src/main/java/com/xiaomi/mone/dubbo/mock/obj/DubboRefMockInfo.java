/*
 * Copyright 2020 XiaoMi.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at the following link.
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.xiaomi.mone.dubbo.mock.obj;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class DubboRefMockInfo {

    private String interfaceName;

    private String group = "";

    private String version = "";

    private List<String> methods;

    private String mockUrlPrefix;

    private boolean enable;

    private Map<String, String> mockUrlMap;

}
