# crawler

## structure
The main component is [`com.example.crawler.CrawlerImpl`](https://github.com/temporarygithubaccount/crawler/blob/master/src/main/java/com/example/crawler/CrawlerImpl.java), which is glueing together all the business logic.

It has three thread pools:
* a  bigger one to visit webpages and extract the links of interest [runs this code](https://github.com/temporarygithubaccount/crawler/blob/master/src/main/java/com/example/crawler/pagecrawler/PageCrawler.java)
* a single-thread pool responsible for enqueuing the links extracted from the pages in a thread-safe queue
* the main thread  responsible for dequeuing the elements from the thread and submit asynchronous work to the first thread pool


There are comments sparse in the codebase with possible further improvements (e.g. including retry logic, moving some configuration to external size)

## tests
Sample main class: [`com.example.crawler.Main`](https://github.com/temporarygithubaccount/crawler/blob/master/src/main/java/com/example/crawler/Main.java)

Unit test are available. Almost all the tested classes are tested comprehensively, unfortunately I ran out of time and (very few) of the classes are missing tests altogether (many of these are trivial anyway, I tested the most important classes).
