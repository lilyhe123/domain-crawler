package com.web;

import java.util.ArrayList;
import java.util.List;

import com.web.api.IExecutor;
import com.web.api.IHtmlConsumer;
import com.web.api.IHtmlDownloader;
import com.web.api.ILinkExtractor;
import com.web.api.IURLFrontier;
import com.web.executor.ExecutorFactory;
import com.web.executor.TaskExecutor;
import com.web.impl.Consts;
import com.web.impl.HtmlConsumer;
import com.web.impl.HtmlDownloader;
import com.web.impl.LinkExtractor;
import com.web.impl.URLFrontier;

public class WebCrawler {
    private final IURLFrontier frontier;
    private final List<IExecutor> executorList = new ArrayList<>();

    public WebCrawler(String startUrl, boolean useVirtualThread) {
        ILinkExtractor extractor = new LinkExtractor();
        IHtmlConsumer consumer = new HtmlConsumer();
        TaskExecutor taskExe = ExecutorFactory.createExecutor(Consts.DOWNLOAD_POOL_SIZE, useVirtualThread);
        IHtmlDownloader downloader = new HtmlDownloader(extractor, consumer, taskExe);
        frontier = new URLFrontier(startUrl, downloader);
        extractor.setFrontier(frontier);
        executorList.add(frontier);
        executorList.add(downloader);
        executorList.add(extractor);
        executorList.add(consumer);
    }
    public void run() throws Exception {
        frontier.start();
        while(!frontier.hasFatalError() && !frontier.isCompleted()) {
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
        String startUrl = null;
        boolean useVirtualThread = false;
        switch (args.length) {
            case 1 -> startUrl = args[0];
            case 2 -> {
                startUrl = args[0];
                if (args[1].equals("-useVirtualThread")) {
                    useVirtualThread = true;
                } else {
                    usage();
                }
            }
            default -> usage();
        }
        WebCrawler crawler = new WebCrawler(startUrl, useVirtualThread);
        crawler.run();
    }
    public static void usage() {
        System.out.println("WebCrawler <startUrl> [-useVirtualThread]");
            System.out.println(1);
    }
}