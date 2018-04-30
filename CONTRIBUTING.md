# I like this project and think I can make some improvements
Fantastic.  Before you get started, make sure to read over the templates for issues and pull requests so you vaguely understand the 
process of submitting work.  Now, I know the hardest part of trying to contribute to any project is getting your head around the basic
flow of data through its system.  I'm going to provide a stupidly oversimplified view of how Jetedge works under the covers in order 
to help slingshot you into contributing.

## Basics of Jetedge.
All of the magic in Jetedge occurs when the `build()` command on `PojoGeneratorBuilder` is run.  Before this point, users define the 
target class that they're trying to build along with their desired limiters.  The builder class will keep all the limiters in a tree 
structure based on the dot-delimited path that is provided as part of `andLimitField()` method.  Once `build` is called, the builder
class will use the `PojoAnalyzer` that has been specified (or the default that looks for setters if nothing has been specified) to 
determine the return type of every settable field within the current class.  This collection of return types, in conjunction with 
the dot-delimited field name for that object, are then sent to the FieldDataGenerator where Jetedge will align all of the provided 
Limiters with this flow of fields.  For any field that is _not_ successfully mapped, it will be provided with a default (or it will be
ignored if the evaluation has been set to lazy).  If the return type is non-simple (i.e. its another layer of a class) then a new 
`PojoGenerator` is stood up and the process repeated for this lower level.  This continues until Jetedge has exhausted all possible 
branches and has a giant mapping of Limiters to their appropriate setter methods which is compressed to a series of `Supplier`/`Consumer` 
pairs. 

# .....What?
Just re-read that section 30 times while starring at the code and you'll make sense of it.  If you're looking for easy places to jump in, 
just builder more Limiters: they're the main way no functionallity is added to Jetedge.
