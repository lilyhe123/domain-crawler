<p align="left">
	<a href="https://github.com/lilyhe123/domain-crawler/actions/workflows/maven.yml"><img alt="Actions status" src="https://github.com/lilyhe123/domain-crawler/actions/workflows/maven.yml/badge.svg"</a>
</p>
		
## Requirements
Write a singe-domain crawler. Given a starting URL, the crawler should visit each URL and download each page it finds on the same domain. 

The crawler should be limited to one domain - so when you start with *https://www.bbc.com/*, it would crawl all pages on the bbc.com website, but not follow external links, for example to twitter.com or nytimes.com etc.

## High-level Design
Starting from the given URL, the crawler downloads the page and extracts all links in that page. Follow the new local links the crawler can download more pages.
Repeat the process until all pages in the same domain have been crawled.

<img width="765" alt="image" src="https://github.com/user-attachments/assets/2c20b893-d891-4e1f-809c-a69a3adffd82" />

## Deep Dive
### Thread pool or virtual thread
Downloading pages from a remove server via http requests is an IO-bound opeation. It's a suitable scenario to use light-weight virtual thread. But we still need limit concurrent execution to prevent overwhelming remote server with too many requests. We use Semaphore for concurrency control.

The crawler offers two options: 1). threadpool with fixed OS threads, 2). vitrual threads with Semaphore for concurrency control.

By default it use thread pool. Add cmd argument '-useVirtualThread' to switch to use vitrual threads.


## How to Run
### Prerequisites
- JDK 23 or later
- Maven

### Setup and Run
1. Clone or create the project.
2. Run `mvn clean install` to build and test.
3. Running test via `mvn test`.

### Running the Application
```bash
mvn exec:java -Dexec.mainClass="com.web.WebCrawler" -Dexec.args="https://bbc.com/" 

```

## TODO
- add logger
- more status
- add rate Limiter

