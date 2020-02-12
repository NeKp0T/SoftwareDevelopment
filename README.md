
# Nesh

Linux build:

[![Build Status](https://travis-ci.com/NeKp0T/SoftwareDevelopment.svg?branch=shell)](https://travis-ci.com/NeKp0T/SoftwareDevelopment)

Windows build:

[![Build status](https://ci.appveyor.com/api/projects/status/chrxlvoewfo1figf?svg=true)](https://ci.appveyor.com/project/NeKp0T/softwaredevelopment)

nesh is a shell that lacks almost every feature a worthy shell should have.

Looks like a stripped down bash, tries to work accordingly but fails miserably.

## Features

Present features are:

* has environment variables 
    * inherited from parent process's system environment variables
    * inherited by children processes
* can run external programs
* supports pipes
    * runs programs in pipe chain consequentially though
    * stores outputs in ordinary temp files
* supports double and single quotes that work like in bash!
    * and no other ways of escaping characters
* has a variety of builtins (like, 5 of them)
* ~~supports interactive programs~~ partially supports interactive programs?
    * no way of sending them Ctrl+D without closing nesh though

## Usage

### Compile & run

Compile and run with 

`./gradlew run -q --console=plain` (linux/unix/whatever)

`gradlew.bat run -q --console=plain` (windows)

(arguments supress gradle's output)

### Test

To run tests use

```./gradlew test```

Some tests rely on having git installed and present in `PATH`. 

Also some tests work only in unix-like systems. I tried my best to identify them and make them always pass on windows, 
but probably there are some more.

### Docs

To generate docs run

```./gradlew dokka```

Docs will appear in a folder `build/dokka/nesh`      

## Guide

### Special variables

Use `PWD` to change working directory manually (cd is not implemented)

Set `NEXIT` to nonempty string in order to close CLI cycle (or use `exit` builtin)

### Builtins

TODO

cat, echo, wc, exit, pwd