/**
 * Copyright 2018 Mike Bray (mrbtrash2@btinternet.com)
 * 
 * Based on work by hleofxquotes@gmail.com 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.moneydance.modules.features.jasperreports;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.jar.JarFile;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.mrbutil.MRBDirectoryUtils;



public class JasperUtils {
 

    static final File getLauncherFile() {
        File direct = MRBDirectoryUtils.getExtensionDataDirectory(Constants.PROGRAMNAME);
        List<File> dirs = new ArrayList<>();
        dirs.add(direct);
        FileFilter filter = new FileFilter() {

            @Override
            public boolean accept(File pathname) {
                if (!pathname.isFile()) {
                    return false;
                }

                String name = pathname.getName();
                if (!name.endsWith(".jar")) {
                    return false;
                }

                boolean rv = false;
                try {
                    try (JarFile jarFile = new JarFile(pathname)) {
                        rv = true;
                   }
                } catch (IOException e) {
                	Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.DETAILED, e.getMessage());
                }

                return rv;
            }
        };

        List<File> jarFiles = new ArrayList<>();
        for (File dir : dirs) {
        	Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY,"JASPER - looking for exec jar file in dir=" + dir.getAbsolutePath());
            File[] files = dir.listFiles(filter);
            jarFiles.addAll(Arrays.asList(files));
        }

        Comparator<File> comparator = new Comparator<File>() {
            @Override
            public int compare(File f1, File f2) {
                int rv = 0;

                // hleOfxQuotes-gui-Build_20180817_225-exec.jar
                int b1 = getBuildNumber(f1);
                int b2 = getBuildNumber(f2);
                Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY,"JASPER - f1=" + f1.getName() + ", b1=" + b1);
                Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY,"JASPER - f2=" + f2.getName() + ", b2=" + b2);
                if ((b1 > 0) && (b2 > 0)) {
                    rv = b1 - b2;
                    rv = -rv;
                } else {
                    long m1 = f1.lastModified();
                    long m2 = f2.lastModified();
                    Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY,"JASPER - f1=" + f1.getName() + ", m1=" + m1);
                    Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY,"JASPER - f2=" + f2.getName() + ", m2=" + m2);
                    rv = (int) (m1 - m2);
                    rv = -rv;
                }

                Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY,"JASPER- compare exec f1=" + f1.getName() + ", f2=" + f2.getName() + ", rv=" + rv);

                return rv;
            }

            private int getBuildNumber(File file) {
                int buildNumber = 0;
                String name = file.getName();
                String[] tokens = name.split("-");
                if (tokens.length == 4) {
                    String buildString = tokens[2];
                    tokens = buildString.split("_");
                    if (tokens.length == 3) {
                        try {
                            buildString = tokens[2];
                            buildNumber = Integer.valueOf(buildString);
                        } catch (NumberFormatException e) {
                        	Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY,e.getMessage());
                            buildNumber = 0;
                        }
                    }
                }
                return buildNumber;
            }
        };
        Collections.sort(jarFiles, comparator);

        Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY,"MDJasperServer- found " + jarFiles.size() + " exec jar file(s) ...");
        for (File jarFile : jarFiles) {
        	Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY,"MDJasperServer - exec jarFile=" + jarFile.getAbsolutePath());
        }
        File jarFile = null;
        if (jarFiles.size() <= 0) {
        	Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY, "Cannot find the rhumba exec jar file.");
        } else {
            jarFile = jarFiles.get(0);
            Main.debugInst.debug("JasperUtils","getLauncherFile",MRBDebug.SUMMARY,"MDJasperServer- Jasper exec jar file=" + jarFile.getAbsolutePath());
        }

        return jarFile;
    }



}
