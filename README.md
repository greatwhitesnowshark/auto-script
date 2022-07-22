# auto-script

This is a unique project that was used for automatically compiling fully-scripted NPCs and quests from two different sources into new JavaScript files.

The first way it can accomplish this task is by analyzing `.msb` files, which are the unique extension of the packet sniffer used for gathering packet
data from the target application. The decoding methods and client codes used in this application are specific to the target application and will not work
in other contexts. In short, this is only useful as a demo showing off old code and perhaps for taking bits and pieces of the logic for other purposes.

The second way, the more useful way, that this project can create JavaScript files from an input source is that it can ->
  -> Convert PYTHON to JavaScript

Syntactically, this was a f*cking hassle to do because of the need to account for an individual's coding preferences - that said, I would claim 90-95% of 
cases converted by the final version of this program do not require review after processing and should run without error the first time. There are some
uncommon situations which may not be accounted for but hey, that's what the coder (you) is here for.

This project was used to convert over 13,000 python scripts designed for a target application into javascript equivalent files that were then used
by my own application instead because we had the same type of application but my script engine wants javascript and I didn't have any scripts of my own. 

:)
