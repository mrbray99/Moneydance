With 2019 The Infinite Kind (TIK) has extended the installation method for Windows based machines.  If you currently use Moneydance on Windows 7 or earlier then you will continue to use the EXE installation method as described below.  If you are using Windows 8.1 or 10 then it is recommended that you use the APPX method.

##APPX Method##

The so called 'APPX' method utilises the Windows Application Store and implements the Microsoft Universal Windows Platform.  The application is packaged into a single package with an extension of .appx.  When you download this and double click on it Windows will either install or update the app.  The files in the package are copied into a folder structure in c:\Program Files\WindowsApps.

You might be interested in reading about the Microsoft Strategy for applications at [Microsoft Universal Windows Platform Strategy](https://docs.microsoft.com/en-us/windows/uwp/get-started/universal-application-platform-guide).

More importantly new features introduced by Microsoft will, most likely, rely on UWP.  Have a look at [Desktop Bridge](https://docs.microsoft.com/en-us/windows/uwp/porting/desktop-to-uwp-root).

I strongly suggest you uninstall earlier versions of Moneydance before installing the APPX version.  **Note:** Ensure you backup the folder c:\Users\{userid}\.moneydance before doing this.  The folder should not be touched by the install but it is worth protecting your data.

##EXE Method##

Moneydance has used a Microsoft Installer package, i.e. a package with an extension of .exe, for a long time.  These installers copy program files to the folder c:\Program Files\Moneydance.  When you update an installation the new files overwrite existing files. With 2019 TIK has updated the Java installation to Java 11 (from 1.8) and the associated .jar files (contains the library functions for Java).

**I strongly recommend that you uninstall previous versions of Moneydance before installing 2019**

[Back](Home)