/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.hadoop.hive.ql.hooks;

import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hive.common.JavaUtils;
import org.apache.hadoop.hive.conf.HiveConf;
import org.apache.hadoop.hive.conf.HiveConf.ConfVars;
import org.apache.hadoop.hive.ql.exec.Utilities;

public class HookUtils {
  /**
   * Returns the hooks specified in a configuration variable.  The hooks are returned
   * in a list in the order they were specified in the configuration variable.
   *
   * @param conf        Configuration object
   * @param hookConfVar The configuration variable specifying a comma separated list
   *                    of the hook class names.
   * @param clazz       The super type of the hooks.
   * @return            A list of the hooks cast as the type specified in clazz,
   *                    in the order they are listed in the value of hookConfVar
   * @throws ClassNotFoundException
   * @throws IllegalAccessException
   * @throws InstantiationException
   */
  public static <T extends Hook> List<T> getHooks(HiveConf conf,
      ConfVars hookConfVar, Class<T> clazz)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException  {
    String csHooks = conf.getVar(hookConfVar);
    List<T> hooks = new ArrayList<T>();
    if (csHooks == null) {
      return hooks;
    }

    csHooks = csHooks.trim();
    if (csHooks.equals("")) {
      return hooks;
    }

    String[] hookClasses = csHooks.split(",");
    for (String hookClass : hookClasses) {
        T hook = (T) Class.forName(hookClass.trim(), true,
                Utilities.getSessionSpecifiedClassLoader()).newInstance();
        hooks.add(hook);
    }

    return hooks;
  }

  public static String redactLogString(HiveConf conf, String logString)
      throws InstantiationException, IllegalAccessException, ClassNotFoundException {

    String redactedString = logString;

    if (conf != null && logString != null) {
      List<Redactor> queryRedactors = getHooks(conf, ConfVars.QUERYREDACTORHOOKS, Redactor.class);
      for (Redactor redactor : queryRedactors) {
        redactor.setConf(conf);
        redactedString = redactor.redactQuery(redactedString);
      }
    }

    return redactedString;
  }
}
