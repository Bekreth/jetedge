# Java Test Data Generator #

[![Build Status](https://travis-ci.org/Bekreth/java-test-data-generator.svg?branch=master)](https://travis-ci.org/Bekreth/java-test-data-generator)

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
PojoGenerator<YourClass> generator = new PojoGeneratorBuilder<>(YourClass.class)
                .andLimitField("fieldName", new IntegerLimiter(10))
                .analyzePojo();
```
With this additional line of code, you've told JTDG that you only 
want numbers 0-9 to be randomly picked and put into the field 
"fieldName" in `YourClass` POJO. A simple limiter exists for all
primitives, String, and List. In addition to these Limiters, I've
also provided a RegexLimiter (of which I'm quite proud of)  

```
PojoGenerator<YourClass> generator = new PojoGeneratorBuilder<>(YourClass.class)
                .andLimitField("phoneNumber", new RegexLimiter("(\\d{3})-\\d{3}-\\d{4}"))
                .analyzePojo();
```
This is a quick example of how to use the RegexLimiter to create 
phonenumbers (A common use for regex).  This feature is not fully
implemented, but it does provide special characters `\s\S\d\D .`,
character ranges `[somechars]`, anti-character ranges `[^notthis]`,
and quantity `{3,4}`, `{2}`, `{5,}`.  I figured this would be good
enough to get you started on using Regex strings.


But now you're asking yourself, "I have a nested class, how do I
configure a field in that nested class?"

```
PojoGenerator<YourClass> generator = new PojoGeneratorBuilder<>(YourClass.class)
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
PojoGenerator<YourClass> generator = new PojoGeneratorBuilder<>(YourClass.class)
                .andLimitField("fieldYouWillLimit", new YourLimiter())
                .analyzePojo();
```

Combine this with the `NestedLimiter` in order to dig down into
the POJO structure.

"But what if I don't want to make a Limiter?" I hear you asking,
"I just want you to fill my POJOs with a set of classes I've 
already made!"  Luckily for you, I've already gone through and
given you the power to do this.
```
PojoGenerator<YourClass> generator = new PojoGeneratorBuilder<>(YourClass.class)
                .andLimitField("fieldYouWillLimit", ObjectLimiter.ofObjects(ListOfYourObjects))
                .analyzePojo();
```
In addition to an easy-to-use static interface, you can also extend 
the ObjectLimiter class if you plan on using the same list of Objects
for several data generators. However if you need to reliably create 
the same 15 random POJOs (And don't want to fill them out yourself
as part of an ObjectLimiter), you can easily clone your generator 
for reuse.
```
PojoGenerator<YourClass> generator1 = new PojoGeneratorBuilder<>(YourClass.class);
PojoGenerator<YourClass> generator2 = generator1.clone();

generator1 = generator1.analyzePojo();
generator2 = generator2.analyzePojo();
```
This produces 2 generators that will return the exact same objects when called.
I'm certain on the most observant of you noticed that you call to analyze
your pojo AFTER you clone.  This is because during the analysis phase,
suppliers are created with the a `Random` and it is quite cumbersome to 
replace all these values.

This is great and all, but what if you want to seed a single generator that 
will then return the same object every time you hit it (you know, the way 
`Random` does when you seed it)?  Welp, then all you have to do is seed your
generator.
```
PojoGenerator<YourClass> generator = new PojoGenerator<>(YourClass.class, intSeed)
                .analyzePojo();
```
If you define this generator in a `@Before` in your unit tests, it will then
always return A, then B, then C and so on.  I will warn you though, 
**IF YOU CHANGE YOUR POJO IN ANY WAY, THIS BREAKS DOWN!**

I've done as much as I can to make this easy, but if you write a bunch of
unit tests expecting a generator to pop out A->B->C and then change your
POJO structure, all of those test will fail because the Random seed being
passed around will have its path altered and instead your output will be
K->?->3.

## PojoAnalyzer ##
By default, the PojoGeneratorBuilder class uses the DefaultPojoAnalyzer
(whozah!).  This Analyzer uses the setter methods that exist for your Pojo
in order to find and populate them even if they are private methods.  In
addition to this default, there is a FieldPojoAnalyzer that looks for and
sets all of your Pojos directly on the field.

```
PojoGenerator<YourClass> generator =
                new PojoGeneratorBuilder(YourClass.class, new FieldPojoAnalyzer())
                .analyzePojo();
```

I would recommend not using this particular anaylzer on a matter of principle
(private fields should be left alone).  It is not my place to disuade anyone
from taking this particular course of action, but know that I judge you, and I
believe my opinion to be valuable.

## How Fast is JDTG? ##
Damn fast.  With a reasonably complex POJO (multiple layers, lists, datatypes,
and regex expressions), JDTG can whip out 1,000,000 POJOs in about 47 seconds.
It can handle whatever you want to through at it.

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

