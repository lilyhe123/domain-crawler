rm -rf data/*
mvn exec:java -Dexec.mainClass="com.web.WebCrawler" 2>&1 | tee 11.out
