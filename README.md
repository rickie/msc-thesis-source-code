# Refaster templates RxJava to Reactor migration

This repository contains [Refaster templates](http://errorprone.info/docs/refaster) that can be run with [Error Prone](https://github.com/google/error-prone).
<br/>
Note that some Refaster templates use features that are currently only available in the [Picnic fork](https://github.com/picnicSupermarket/error-prone) of Error Prone. 

## This repository is still a work in progress. The content is there, but the project itself requires extra attention.

## Goal of the Refaster templates
The templates are created and used for a thesis project that is done at [Picnic Technologies](https://github.com/PicnicSupermarket).
The title of the thesis is: <br/>
<b>Towards Automated Library Migrations with Error Prone and Refaster</b>
<br/><br/>
For the validation of the research, we performed a migration on two codebases. 
One codebase is provided by Picnic and the other is open source; [Gravitee](https://github.com/rickie/gravitee-access-management).

## General remarks
- The annotation `@CanTransformToTargetType` can only be used together with the Picnic fork
- The `*TestInput.java` and `*TestOutput.java` are taken from a private codebase that is yet to be open sourced. The `input` and `output` files are used to validate the Refaster templates.  


