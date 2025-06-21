<p align="left">
	<a href="https://github.com/lilyhe123/domain-crawler/actions/workflows/maven.yml"><img alt="Actions status" src="https://github.com/lilyhe123/domain-crawler/actions/workflows/maven.yml/badge.svg"</a>
</p>
		
## Requirements
Write a singe-domain crawler. Given a starting URL, the crawler should visit each URL and download each page it finds on the same domain. 

The crawler should be limited to one domain - so when you start with *https://www.bbc.com/*, it would crawl all pages on the bbc.com website, but not follow external links, for example to twitter.com or nytimes.com etc.

## Design
Starting from the given URL, the crawler downloads the page and extracts all links in that page. Follow the new local links the crawler can download more pages.
Repeat the process until all pages in the same domain have been crawled.

<img width="1451" alt="image" src="https://github.com/user-attachments/assets/2f8cec54-9d38-44a8-8674-c8ed07cdc7e5" />


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
mvn exec:java -Dexec.mainClass="com.web.WebCrawler" -Dexec.args="https://bravenewgeek.com/" 

```

## TODO
- add logger
- more status
- use vthread in downloader, compare results with using thread pool
- add rate Limiter


```bash
mvn exec:java -Dexec.mainClass="com.web.WebCrawler" -Dexec.args="https://bravenewgeek.com/" 2>&1 | tee 11.out


sample domains: "https://bravenewgeek.com", "https://lilyhe123.github.io/", "https://www.bbc.com/";

Errors:
Got IOException. Connection reset. url: https://lilyhe123.github.io
java.net.SocketException: Connection reset
	at java.base/sun.nio.ch.NioSocketImpl.implRead(NioSocketImpl.java:318)
	at java.base/sun.nio.ch.NioSocketImpl.read(NioSocketImpl.java:345)
	at java.base/sun.nio.ch.NioSocketImpl$1.read(NioSocketImpl.java:794)
	at java.base/java.net.Socket$SocketInputStream.read(Socket.java:1025)
```
