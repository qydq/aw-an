package com.lyue.aw_an.activity;

import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.an.an_base.mvp.base.SuperActivity;
import com.lyue.aw_an.R;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

/**********************************************************
 * @文件名称：CoordinatorLayoutActivity
 * @文件作者：staryumou@163.com
 * @创建时间：2016/10/12
 * @文件描述：null
 * @修改历史：2016/10/12
 **********************************************************/
public class PachongActivity extends SuperActivity {

    private TextView tv = null;
    private static String url = "http://bgwan.blog.163.com/blog/static/23930101620169131134575/";
    private static Document doc;

    @Override
    public void initView() {
        setContentView(R.layout.sst_activity_pachong);
        tv = (TextView) findViewById(R.id.tv);
        tv.setText("等待测试！");

        // 开辟一个线程
        new Thread(runnable).start();

    }

    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            /**
             * 要执行的操作
             */
            // 执行完毕后给handler发送一个空消息
            Document doc = null;
            try {
                doc = Jsoup.connect("http://bgwan.blog.163.com/blog/static/23930101620169131134575/")
                        .data("query", "Java")
                        .userAgent("Mozilla")
                        .cookie("auth", "token")
                        .timeout(3000)
                        .post();
                String title = doc.title();
                Element body = doc.body();

                System.out.println(getClass().getName() + "---qydq--title-:" + title);

                Elements links = body.select("a[href]"); //带有href属性的a元素
                Elements pngs = body.select("img[src$=.png]");//扩展名为.png的图片
                Element masthead = body.select("div.nbw-blog-start").first();//class等于masthead的div标签
                System.out.println(getClass().getName() + "---qydq--masthead-:" + masthead.toString());
                System.out.println(getClass().getName() + "---qydq--links-:" + links.toString());
                for (Element link : pngs) {
                    String linkHref = link.attr("href");
                    String linkText = link.text();
                    System.out.println(getClass().getName() + "---qydq--pngs-:" + link);
                }

                Message msg = new Message();
                msg.obj = title;
                msg.obj = body.toString();
                handler.sendMessage(msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    };

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            /**
             * 处理UI
             */
            // 当收到消息时就会执行这个方法
            String title = (String) msg.obj;
            String body = (String) msg.obj;
            tv.setText(body);
        }
    };

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
