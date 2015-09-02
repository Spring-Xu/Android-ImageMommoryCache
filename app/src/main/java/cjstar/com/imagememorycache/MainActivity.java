package cjstar.com.imagememorycache;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import cjstar.com.imagememorycache.image.ImageHelper;
import cjstar.com.memorycachelibrary.MemoryCacheOptions;
import cjstar.com.memorycachelibrary.cache.BitmapMemoryCache;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    ListView mListView;
    List<String> data;
    BitmapMemoryCache cache;
    MyAdapter adapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mListView = (ListView)findViewById(R.id.list_view);
        findViewById(R.id.button).setOnClickListener(this);
        cache = new BitmapMemoryCache(new MemoryCacheOptions.Builder().setmMaxCacheCount(10)
        .setmMaxCacheSize(1024*1).build());
        data = new ArrayList<String>();
        adapter = new MyAdapter(data);
        mListView.setAdapter(adapter);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button:
                add10Images();
                adapter.notifyDataSetChanged();
                break;

            default:break;
        }
    }

    String images1[] = {
            "http://img0.imgtn.bdimg.com/it/u=3568586866,3049182084&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1995630635,3244521127&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=803472824,3769148041&fm=21&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=45651822,1492646809&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=3087361280,1780887017&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=3179184432,1658518130&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=1327719982,2473806388&fm=21&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=3789269170,1599468517&fm=21&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=1717616334,2840906603&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=2244661108,274090057&fm=21&gp=0.jpg",
            "http://pic4.nipic.com/20090912/3114753_125944045705_2.jpg",
            "http://e.hiphotos.baidu.com/zhidao/pic/item/38dbb6fd5266d01695ab94bf952bd40734fa35f2.jpg",
            "http://attach.bbs.miui.com/forum/201408/29/123450r617238484s76q78.jpg",
            "http://www.bz55.com/uploads/allimg/140327/137-14032G62044.jpg",
            "http://d.3987.com/ziyanghua_140731/003.jpg",
            "http://bbsimg0.27.cn/forum/201109/06/0115400e0w7gk1kcyzubh4.jpg",
            "http://www.33.la/uploads/20140407pic/545.jpg",
            "http://img3.douban.com/view/photo/raw/public/p1678149275.jpg",
            "http://p1.gexing.com/shaitu/20120710/1812/4ffc001335cf3.jpg",
            "http://www.33.la/uploads/20130523tpxh/5156.jpg",
            "http://img2.imgtn.bdimg.com/it/u=289621088,837225378&fm=21&gp=0.jpg",
            "http://img3.imgtn.bdimg.com/it/u=4216414931,1476403730&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=2250676209,3213938069&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=807480838,2539247440&fm=21&gp=0.jpg",
            "http://img4.imgtn.bdimg.com/it/u=2213157682,2700037810&fm=21&gp=0.jpg",
            "http://img5.imgtn.bdimg.com/it/u=183340984,94440019&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=3614330485,2500065287&fm=21&gp=0.jpg",
            "http://img0.imgtn.bdimg.com/it/u=1619952554,1865779312&fm=21&gp=0.jpg",
            "http://img1.imgtn.bdimg.com/it/u=1974718137,1693856044&fm=21&gp=0.jpg",
            "http://img2.imgtn.bdimg.com/it/u=774054403,1115172007&fm=21&gp=0.jpg",
            "http://e.hiphotos.baidu.com/image/pic/item/37d12f2eb9389b50ab7149bf8135e5dde6116e55.jpg",
            "http://d.hiphotos.baidu.com/image/pic/item/ae51f3deb48f8c54675b6cc23e292df5e1fe7f57.jpg",
            "http://c.hiphotos.baidu.com/image/pic/item/730e0cf3d7ca7bcba83d6ec2ba096b63f724a89d.jpg",
            "http://www.huabian.com/uploadfile/2014/0701/20140701091157427.jpg",
            "http://www.huabian.com/uploadfile/2014/0701/20140701091153388.jpg",
            "http://g.hiphotos.baidu.com/zhidao/pic/item/9f510fb30f2442a79fbc68ded343ad4bd113021e.jpg",
            "http://attach.bbs.miui.com/forum/201402/21/120043wsfuzzuefyasz3fe.jpg",
            "http://fe.topit.me/e/25/b3/1164209364436b325eo.jpg",
            "http://h.hiphotos.baidu.com/zhidao/pic/item/6a63f6246b600c3320b14bb3184c510fd8f9a185.jpg",
            "http://attach.bbs.miui.com/forum/201502/08/021410qwvwbpdkiasdhwha.jpg"

    };

    private void add10Images(){
        if(data.size()<10){
            for(String item:images1){
                data.add(item);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private class  MyAdapter extends BaseAdapter{
        List<String> data;
        public MyAdapter(List<String> images){
            data = images;
        }
        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if(convertView==null){
                holder = new ViewHolder();
                convertView = LayoutInflater.from(MainActivity.this).inflate(R.layout.list_item, null);
                holder.mTview = (TextView)convertView.findViewById(R.id.text);
                holder.mImgView = (ImageView)convertView.findViewById(R.id.image);
                convertView.setTag(holder);

            }else{
                holder = (ViewHolder)convertView.getTag();
            }

            ImageHelper.getInstance().showImage(holder.mImgView, data.get(position), R.mipmap.ic_launcher);
            holder.mTview.setText(data.get(position));
            return convertView;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        class  ViewHolder{
            ImageView mImgView;
            TextView mTview;
        }
    }
}
