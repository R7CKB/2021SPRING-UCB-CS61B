# Gitlet Design Document

**Name**:R7CKB

can’t analyze Chinese

## Classes and Data Structures

### Commit

#### Fields

1. Field 1
2. Field 2

### Repository

#### Fields

1. Field 1
2. Field 2

## Algorithms

## Persistence

### First thing to do

> you need to figure what file structure is,this is important and vital, for it influence your classes designs.
> you can refer the real git file structure to get an idea of what to store and how to store it.

[Reference](https://paper.dropbox.com/doc/Gitlet-Persistence-zEnTGJhtUMtGr8ILYhoab)

**Key Idea: Design with persistence in mind from the start.**

- You might be tempted to figure out your classes and data structures for an idealized Gitlet where we don’t need to
  worry about persistence first,
  and then figure out persistence after.
    - Don’t do this, it’ll be annoying and painful.
- Keep the fact that you need to be able to load/save things from disk in mind when picking your classes and data
  structures.

you can't 