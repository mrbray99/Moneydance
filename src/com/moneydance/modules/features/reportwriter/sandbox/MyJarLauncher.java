/**
 * Copyright 2020 Mike Bray (mrbtrash2@btinternet.com)
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

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;

import com.moneydance.modules.features.mrbutil.MRBDebug;
import com.moneydance.modules.features.reportwriter.Constants;
import com.moneydance.modules.features.reportwriter.Main;



public class MyJarLauncher{
     private final ClassLoader classLoader;
     private Method setEnvironment;
     private Method compileReport;
     private Method fillReport;
     private Method viewReport;
     private Method setExtension;
     private Object instance;

    private final File execJarFile;

     public MyJarLauncher(File execJarFile) throws Exception {
        this.execJarFile = execJarFile;
        try {
 	        URL [] urls = new URL[] {execJarFile.toURI().toURL()};
	        this.classLoader = createClassLoader(urls);
        }
        catch (MalformedURLException e) {
        	e.printStackTrace();
        	throw new Exception(e);
        }
       	getReportServer();
      }


    protected MyLaunchedURLClassLoader createClassLoader(URL[] urls) throws Exception {
        final ClassLoader parent = ClassLoader.getSystemClassLoader();
        // final ClassLoader parent = null;
        MyLaunchedURLClassLoader myClassLoader = new MyLaunchedURLClassLoader(urls, parent);

         return myClassLoader;
    }

 
    public ClassLoader getClassLoader() {
        return classLoader;
    }

    public File getExecJarFile() {
        return execJarFile;
    }
    

    public Method getSetExtension() {
		return setExtension;
	}


	public void setSetExtension(Method setExtension) {
		this.setExtension = setExtension;
	}


	public Method getSetEnvironment() {
		return setEnvironment;
	}


	public void setSetEnvironment(Method setEnvironment) {
		this.setEnvironment = setEnvironment;
	}


	public Method getCompileReport() {
		return compileReport;
	}


	public void setCompileReport(Method compileReport) {
		this.compileReport = compileReport;
	}


	public Method getFillReport() {
		return fillReport;
	}


	public void setFillReport(Method fillReport) {
		this.fillReport = fillReport;
	}


	public Method getViewReport() {
		return viewReport;
	}


	public void setViewReport(Method viewReport) {
		this.viewReport = viewReport;
	}


	public Object getInstance() {
		return instance;
	}


	public void setInstance(Object instance) {
		this.instance = instance;
	}


	private void getReportServer() throws WriterServiceException {
        try {
            Class<?> clz = classLoader.loadClass(Constants.CLASSNAME);
            Class [] cArg = new Class[2];
            cArg[0] = String.class;
            cArg[1] = Connection.class;
            Constructor ctor = clz.getDeclaredConstructor();
            instance = ctor.newInstance();
            setExtension = clz.getDeclaredMethod(Constants.SETEXTENSION, String [].class);
            setEnvironment = clz.getDeclaredMethod(Constants.SETENVIRONMENT,String.class);
            setEnvironment.setAccessible(true);
            Main.rwDebugInst.debug("MyJarLauncher","getReportServer",MRBDebug.DETAILED,"method got "+setEnvironment);
            compileReport = clz.getDeclaredMethod(Constants.COMPILEREPORT,String.class);
            compileReport.setAccessible(true);
            Main.rwDebugInst.debug("MyJarLauncher","getReportServer",MRBDebug.DETAILED,"method got "+compileReport);
            fillReport = clz.getDeclaredMethod(Constants.FILLREPORT,cArg);
            fillReport.setAccessible(true);
            Main.rwDebugInst.debug("MyJarLauncher","getReportServer",MRBDebug.DETAILED,"method got "+fillReport);
            viewReport = clz.getDeclaredMethod(Constants.VIEWREPORT,String.class);
            viewReport.setAccessible(true);
            Main.rwDebugInst.debug("MyJarLauncher","getReportServer",MRBDebug.DETAILED,"method got "+viewReport);
     } catch (Exception e) {
        	e.printStackTrace();
            throw new WriterServiceException(e);
        }
    }
}

