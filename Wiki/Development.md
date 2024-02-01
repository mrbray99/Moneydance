[Home](Home)>[Moneydance](Moneydance Information)>Development

# Developing Extensions for Moneydance

## Introduction

This section of the Wiki presents my views of how to develop extensions for Moneydance.  The Infinite Kind has provided some information but most of what you need is determined from looking at examples.

The information provided is basically in two parts:

 1. The Development Page [Link](http://infinitekind.com/developer)
 2. The Development Kit which contains the API and a blank extension to get you started.

The first thing is to download the development kit and set up a project in your chosen IDE.  I use Eclipse and Windows so details here are to do with this environment.  I have the Java development kit, ANT and Git.

The information presented here is an addendum to anything provided by Infinite Kind.

What is missing is any documentation on what sequence to call methods when updating the data file.  In 2014 this seemed to be quite straight forward, however, with 2015 this is not the case.  All main objects are extensions of MoneydanceSyncableItem.  What sequence methods should be called has not been documented.  So it is try and see.  What I have presented here is a set of things that I think work.

## Information Provided

The following sections are provided:

* [Development Environment](Development Environment)
* [Debugging](Debugging)
* [Extension Structure](Structure)
* [Specifics](Specifics)