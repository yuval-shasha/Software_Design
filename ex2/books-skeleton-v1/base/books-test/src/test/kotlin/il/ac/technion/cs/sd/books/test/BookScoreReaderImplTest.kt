package il.ac.technion.cs.sd.books.test

class BookScoreReaderImplTest
{
    // For each test in BookScoreInitializerImplTest, check each method of the Reader class

    // Check that every combination of 5 different queries takes at most 30 seconds (not including the setup)

    // Check that the open method in LineStorageFactory is called only once in every query

    // Check for each method that when we pass an invalid id (either book or reviewer) the method returns null

    // Count the number of times the open method in LineStorageFactory is called, and increase the number of milliseconds linearly in the number of calls, starting from 100ms

    // For each test in BookScoreInitializerImplTest, add the maximal version - 1 million reviewers, 100,000 books, every reviewer writes 100 reviews, every book is reviewed by 100 reviewers
}