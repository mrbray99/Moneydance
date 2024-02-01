[Home](Home)

# Upgrading Moneydance to 2015 #

In Moneydance 2015 the way data is stored has changed.  If you have a previous version and upgrade MD will change your files to the new version.  This means that you can not go back to the previous version.

MD uses a folder called **.Moneydance** under your user folder to hold all information and data.  Within that folder is a **Documents** which contains the user data.  Before upgrading you need to backup this folder.

In prior versions the data is in a file called **root.mdinternal**. In 2015 this is moved to a folder called **checkpt**.  In addition there is a folder called **safe** that contains further data.  Make sure that both of these folders are included in your backups.

Should you want to downgrade to a previous version you need to:

1. Delete the checkpt folder
2. Delete the safe folder
3. Reload you root.mdinternal file from your backup

**Note:**  MD will backup your previous files into a zip file in the **archive** folder.