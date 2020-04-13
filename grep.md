# grep

### CL arguments parsing library choice

All libraries do basically the same thing. 
They let programmer define a list of options and generate nice getters for these options' values.

### Argparse4j

Procs: has option name correction (`Unrecognised argument 'tpye'. Did you mean 'type'?'`)

Cons: 4j

### Apache Commons CLI 

Couldn't find anything extra about it. It's [example page](http://commons.apache.org/proper/commons-cli/usage.html) fails
to show any special features. And it's also for java

### kotlin --argparser

"It aims to be easy to use and concise yet powerful and robust" (c) readme

It really is easy to use and powerful. README provides enough examples to write code without consulting documentation.

The only problem is that it has been dead for over a year.

### Clikt

The biggest kotlin library for CLI. 

It has a big site with documentation explaining every last feature. There are quite a lot of features, which makes learning 
a bit complex. 

It actually has a page dedicated to ~~boasting~~ comparision with other libraries: [Why Clikt?](https://ajalt.github.io/clikt/whyclikt/)

In short, Clikt provides a wide set of fully composable transformers, 
which makes building any functionality a matter of a few chained method calls.

Also licensed under Apache License 2.0, so no problems from that side.
  