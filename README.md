# Running

    boot run

or

    boot run -n ${size of grid}

Yes, it should play more nicely when packaged into a .jar.

# Implementation Notes

Using a lazy sequence probably wasn't the wisest approach.

Especially for a data set this small.

I was very tempted to see how this implementation compared
with one built around transducers.

But this approach *does* seem elegant. And efficient enough
(for some value of "enough").

# Requirements

Write a program that prints out a multiplication table of the first 10 prime number.

* The program must run from the command line and print one table to STDOUT.
* The first row and column of the table should have 10 primes, with each cell
  containing the product of the primes for the corresponding row and column.

## Notes

* Consider complexity. How fast does your code run? How does it scale?
* Consider cases where we want *N* primes
* Do not use the Prime class from stdlib (write your own code).
* Write tests. Try to demonstrate TDD/DBB.
* If you're using external dependencies please specify those dependencies and how
  to install them.
* Please _package your code_, OR include running instructions.
