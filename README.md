# Java Test Data Generator
Welcome welcome.

This is a Java library for quickly and easily creating random data
based off of YOUR POJOs.  How quick and easy?

## How to Use JTDG! ##

```
PojoGenerator<YourClass> generator = new PojoGenerator<>(YourClass.class)
                .analyzePojo();
```

This one line of code give the JTDG everything it needs to create
randomized POJOs.  When you use the `analyzePojo()` method of the
PojoGenerator class, it recursively scans through your POJO down 
to its primatives (int, String, boolean, etc...).  After it has 
scanned your POJO, it populates it with generators for each field.

After you've created your the generator, there are 3 ways to get 
data.

- generator.generatePojo();
- generator.generateList(int count);
- generator.generateStream();

Believe it or not, these do what you would expect them to do.
Option 1 will create a single random POJO. Option 2 will return
a List of randomly generated POJOs that you specify.  Lastly, 
option 3 will return a Stream for your POJO so that you can iterate
over it however you want.  

That's all wonderful, but what if you don't want default randomizers 
(Because for some reason you only want random integers from 0 to 10)?
Welp, I've tried my hardest to make it simple and easy to configure
the generator (mostly because I built this to use myself). 

```
PojoGenerator<YourClass> generator = new PojoGenerator<>(YourClass.class)
                .andLimitField("fieldName", new IntegerLimiter(10))
                .analyzePojo();
```
With this additional line of code, you've told JTDG that you only 
want numbers 0-9 to be randomly picked and put into the field 
"fieldName" in `YourClass` POJO. Currently, I plan on having a 
reasonable `Limiter` for ALL the primitive classes.  As you can, 
the current version is 0.1.0-SNAPSHOT: I don't have them all done
yet.  At the moment, `Limiter`s exist for `Integer` and `String`.

But now you're asking yourself, "I have a nested class, how do I
configure a field in that nested class?"

```
PojoGenerator<YourClass> generator = new PojoGenerator<>(YourClass.class)
                .andLimitField("innerFieldName", new NestedLimiter(YourNestedClass.class, new IntegerLimiter(5, -10)))
                .analyzePojo();
```
BOOM! This line tells JTDG that in the class you're giving to the 
generator, that along the class structure there is a class called 
`YourNestedClass` and you want to set its field `innerFieldName` 
to only be between -10 and -5.

Assuming that you want to provide your own `Limiter`, just implement
the `Limiter` class and provide it to the generator like so:
```
PojoGenerator<YourClass> generator = new PojoGenerator<>(YourClass.class)
                .andLimitField("fieldYouWillLimit", new YourLimiter())
                .analyzePojo();
```

Combine this with the `NestedLimiter` in order to dig down into
the POJO structure.


## Default Limiters ##
These are the `Limiter`s that I am/will provide for you in the near
future. 

Limiter | Currently Supported
---|---
IntegerLimiter | True
StringLimiter | True
ShortLimiter | Kinda
BooleanLimiter | False
FloatLimiter | False
DoubleLimiter | False
LongLimiter | False
CharLimiter | False
ListLimiter | Kinda
RegexLimiter | False

Looking at this list, you may realize that not much is currently 
supported in ways of `Limiter`s.  It was my main objective in 
this first iteration to create the plumbing that does the hard 
labor.  I will be regularly working on this project for the next
few months to flush it out and make it much more robust.

## How to Contribute ##

I operate on a pull request model. Fork my repo, make an alteration
create a pull request against the live master branch.  I will have
absolute authority on what gets merged, so be nice and create 
legible code, write unit tests, and fill out Javadocs.  The project
should be able to be built locally with `mvn clean install`, if 
it doesn't, you done goofed.

I work a full time job and play with this project on the side, so
please be patient with me and my response time.

At this stage, if you look at my code, you'll notice I have not
abided by a lot of my rules.  I'm not happy about this, but my #1
goal was to get the initial SNAPSHOT because a lot of people in 
my office are quite excited by this idea (seems its a good one)
and want to contribute starting a month ago when I first came
up with this idea.

## Release Plan ##

It is my plan to have a truly complete version 1 release of this
product up in Maven Central repo around the start of November.

