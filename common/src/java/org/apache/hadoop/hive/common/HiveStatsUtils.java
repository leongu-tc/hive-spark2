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
package org.apache.hadoop.hive.common;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.fs.FileStatus;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;

/**
 * HiveStatsUtils.
 * A collection of utilities used for hive statistics.
 * Used by classes in both metastore and ql package
 */

public class HiveStatsUtils {

  /**
   * Get all file status from a root path and recursively go deep into certain levels.
   *
   * @param path
   *          the root path
   * @param level
   *          the depth of directory to explore
   * @param fs
   *          the file system
   * @return array of FileStatus
   * @throws IOException
   */
  public static FileStatus[] getFileStatusRecurse(Path path, int level, FileSystem fs)
      throws IOException {

    // if level is <0, the return all files/directories under the specified path
    if ( level < 0) {
      List<FileStatus> result = new ArrayList<FileStatus>();
      try {
        FileStatus fileStatus = fs.getFileStatus(path);
        FileUtils.listStatusRecursively(fs, fileStatus, result);
      } catch (IOException e) {
        // globStatus() API returns empty FileStatus[] when the specified path
        // does not exist. But getFileStatus() throw IOException. To mimic the
        // similar behavior we will return empty array on exception. For external
        // tables, the path of the table will not exists during table creation
        return new FileStatus[0];
      }
      return result.toArray(new FileStatus[result.size()]);
    }

    // construct a path pattern (e.g., /*/*) to find all dynamically generated paths
    StringBuilder sb = new StringBuilder(path.toUri().getPath());
    for (int i = 0; i < level; i++) {
      sb.append(Path.SEPARATOR).append("*");
    }
    Path pathPattern = new Path(path, sb.toString());
    return fs.globStatus(pathPattern, FileUtils.HIDDEN_FILES_PATH_FILTER);
  }

}
