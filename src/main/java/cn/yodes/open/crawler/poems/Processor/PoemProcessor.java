package cn.yodes.open.crawler.poems.Processor;

import cn.yodes.open.crawler.poems.pipeline.FilePipeline;
import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.Site;
import us.codecraft.webmagic.Spider;
import us.codecraft.webmagic.processor.PageProcessor;
import us.codecraft.webmagic.selector.Html;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PoemProcessor implements PageProcessor {
    private Site site = Site.me()
            .setDomain("open.yodes.cn/Crawler")
            .setRetrySleepTime(3)
            .setSleepTime(1000);

    @Override
    public void process(Page page) {
        List<String> links = page.getHtml().links().regex("http://www.shicimingju.com/chaxun/list/\\d+.html").all();
        page.addTargetRequests(links);
        page.putField("title", page.getHtml().xpath("//*/h1[@class='shici-title']/text()"));
        page.putField("dynasty", page.getHtml().xpath("//*/div[@class='shici-info']/text()"));
        page.putField("author", page.getHtml().xpath("//*/div[@class='shici-info']/a/text()"));
        page.putField("content", page.getHtml().xpath("//*/div[@class='shici-content']/text()"));
        page.putField("tags", page.getHtml().xpath("//*/div[@class='shici-mark']/a/text()").all());
        page.putField("appreciation", page.getHtml().xpath("//*/div[@class='shangxi-container']/text()"));
    }

    @Override
    public Site getSite() {
        return site;
    }

    public static void main(String[] args) {
        Html html = new Html("http://www.shicimingju.com/chaxun/zuozhe/29.html");
        String[] urlList = html.xpath("//*/div[@class='pagination www-shadow-card']")
                .links().xpath("http://www.shicimingju.com/chaxun/zuozhe/\\s+.html").all().toArray(args);
        Spider.create(new PoemProcessor()).addUrl(urlList)
                .addPipeline(new FilePipeline()).run();

    }
}