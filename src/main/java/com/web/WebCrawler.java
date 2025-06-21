package com.web;

import java.util.ArrayList;
import java.util.List;

import com.web.api.IExecutor;
import com.web.api.IHtmlConsumer;
import com.web.api.IHtmlDownloader;
import com.web.api.ILinkExtractor;
import com.web.api.IURLFrontier;
import com.web.threadpool.HtmlConsumer;
import com.web.threadpool.HtmlDownloader;
import com.web.threadpool.LinkExtractor;

public class WebCrawler {
    private final IURLFrontier frontier;
    private final List<IExecutor> executorList = new ArrayList<>();

    public WebCrawler(String startUrl) {
        ILinkExtractor extractor = new LinkExtractor();
        IHtmlConsumer consumer = new HtmlConsumer();
        IHtmlDownloader downloader = new HtmlDownloader(extractor, consumer);
        frontier = new URLFrontier(startUrl, downloader);
        extractor.setFrontier(frontier);
        executorList.add(frontier);
        executorList.add(downloader);
        executorList.add(extractor);
        executorList.add(consumer);
    }
    public void run() throws Exception {
        frontier.start();
        while(!frontier.hasError() && !frontier.isCompleted()) {
            printStatus();
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {}
        }
        executorList.forEach(IExecutor::shutdown);
    }

    private void printStatus() {
        StringBuilder sb = new StringBuilder();
        executorList.forEach(a->sb.append(a.status()).append("\n"));
        System.out.println(sb);
    }
    public static void main(String... args) throws Exception {
        if (args.length != 1) {
            System.out.println("Pls provide a startUrl.");
            System.out.println(1);
        }
        WebCrawler crawler = new WebCrawler(args[0]);
        crawler.run();
    }
}