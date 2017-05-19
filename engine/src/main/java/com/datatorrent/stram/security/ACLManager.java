/**
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*   http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing,
* software distributed under the License is distributed on an
* "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
* KIND, either express or implied.  See the License for the
* specific language governing permissions and limitations
* under the License.
*/
package com.datatorrent.stram.security;

 import java.io.IOException;
 import java.util.Map;
 import org.apache.hadoop.conf.Configuration;
 import org.apache.hadoop.security.UserGroupInformation;
 import org.apache.hadoop.yarn.api.records.ApplicationAccessType;
 import org.apache.hadoop.yarn.api.records.ContainerLaunchContext;
 import org.apache.hadoop.yarn.conf.YarnConfiguration;

import com.google.common.collect.Maps;

public class ACLManager
{
  public static void setupLoginACLs(ContainerLaunchContext launchContext, Configuration conf) throws IOException
  {
    if (areAclsRequired(conf)) {
      Map<ApplicationAccessType, String> acls = Maps.newHashMap();
      acls.put(ApplicationAccessType.MODIFY_APP, UserGroupInformation.getLoginUser().getUserName());
      launchContext.setApplicationACLs(acls);
    }
  }

  public static boolean areAclsRequired(Configuration conf)
  {
    if (conf.getBoolean(YarnConfiguration.YARN_ACL_ENABLE, YarnConfiguration.DEFAULT_YARN_ACL_ENABLE)) {
      if (!YarnConfiguration.DEFAULT_YARN_ADMIN_ACL.equals(conf.get(YarnConfiguration.YARN_ADMIN_ACL))) {
        return true;
      }
    }
    return false;
  }
}