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

package com.moneydance.modules.features.reportwriter.sandbox;

import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.URLConnection;
import java.security.AccessController;
import java.security.PrivilegedExceptionAction;




class MyLaunchedURLClassLoader extends URLClassLoader {


    public MyLaunchedURLClassLoader(URL[] urls, ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    public URL findResource(String name) {
      URL url = null;
 
        url = super.findResource(name);
       	if (url==null)
       		if (!name.startsWith("/")) {
       			url=findResource("/"+name);
       		}
        return url;
    }
    @Override
    public URL getResource(String name) {
    	URL url = null;
 
        url = super.getResource(name);
       	if (url==null) {
       		if (!name.startsWith("/")) {
       			url=getResource("/"+name);
       		}
       	}
        return url;
    }
    @Override
    public InputStream getResourceAsStream(String name) {
     	InputStream stream = null;
    	stream = super.getResourceAsStream(name);
       	if (stream==null) {
       		if (!name.startsWith("/")) {
       			stream=getResourceAsStream("/"+name);
      		}
       	}
        return stream;
    	
    }
    @Override
    protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
        try {
            try {
                definePackageIfNecessary(name);
            } catch (IllegalArgumentException ex) {
                // Tolerate race condition due to being parallel capable
                if (getDefinedPackage(name) == null) {
                    // This should never happen as the IllegalArgumentException
                    // indicates
                    // that the package has already been defined and, therefore,
                    // getPackage(name) should not return null.
                    throw new AssertionError(
                            "Package " + name + " has already been " + "defined but it could not be found");
                }
            }
            return childFirstLoadClass(name, resolve);
        } finally {
         }
    }

    private Class<?> childFirstLoadClass(String name, boolean resolve) throws ClassNotFoundException {
        // final Class<?> aClass = super.loadClass(name, resolve);
        final Class<?> aClass = myLoadClass(name, resolve);
         return aClass;
    }

    private Class<?> myLoadClass(String name, boolean resolve) throws ClassNotFoundException {
        Class<?> clazz = findLoadedClass(name);

        ClassLoader parentCL = getParent();

        // 2. If the class is not loaded and the class name starts
        // with 'java.' or 'javax.', delegate loading to parent
        if (clazz == null && parentCL != null && (name.startsWith("java.") || name.startsWith("javax.") || name.startsWith("javafx.") )) {
            clazz = parentCL.loadClass(name);

        }

        // 3. If the class is still null, try to load the class from the URL
        // (since we have already taken care of 'java.' and 'javax.'
        if (clazz == null) {
            try {
                clazz = super.findClass(name);
            } catch (ClassNotFoundException e) {
                // don't do anything
            }
        }

        // 4. If the class is still null, let the parent class loader load it.
        // Previously, we allowed 'java.' and 'javax.' classes to be loaded
        // from parent
        if (clazz == null && parentCL != null) {
            clazz = parentCL.loadClass(name);
        }

        // 5. If the class is still null, throw a class not found exception
        if (clazz == null) {
            throw new ClassNotFoundException(name);
        }

        if (resolve) {
            resolveClass(clazz);
        }

        return clazz;
    }

    /**
     * Define a package before a {@code findClass} call is made. This is
     * necessary to ensure that the appropriate manifest for nested JARs is
     * associated with the package.
     * 
     * @param className
     *            the class name being found
     */
    private void definePackageIfNecessary(String className) {
        int lastDot = className.lastIndexOf('.');
        if (lastDot >= 0) {
            String packageName = className.substring(0, lastDot);
            if (getDefinedPackage(packageName) == null) {
                try {
                    definePackage(className, packageName);
                } catch (IllegalArgumentException ex) {
                    // Tolerate race condition due to being parallel capable
                    if (getDefinedPackage(packageName) == null) {
                        // This should never happen as the
                        // IllegalArgumentException
                        // indicates that the package has already been defined
                        // and,
                        // therefore, getPackage(name) should not have returned
                        // null.
                        throw new AssertionError(
                                "Package " + packageName + " has already been defined " + "but it could not be found");
                    }
                }
            }
        }
    }

    private void definePackage(String className, String packageName) {
        try {
            AccessController.doPrivileged((PrivilegedExceptionAction<Object>) () -> {
                String packageEntryName = packageName.replace('.', '/') + "/";
                String classEntryName = className.replace('.', '/') + ".class";
                for (URL url : getURLs()) {
                    try {
                        URLConnection connection = url.openConnection();
                        if (connection instanceof JarURLConnection) {
                            java.util.jar.JarFile jarFile = ((JarURLConnection) connection).getJarFile();
                            if (jarFile.getEntry(classEntryName) != null && jarFile.getEntry(packageEntryName) != null
                                    && jarFile.getManifest() != null) {
                                definePackage(packageName, jarFile.getManifest(), url);
                                return null;
                            }
                        }
                    } catch (IOException ex) {
                        // Ignore
                    }
                }
                return null;
            }, AccessController.getContext());
        } catch (java.security.PrivilegedActionException ex) {
            // Ignore
        }
    }


}
