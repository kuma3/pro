import twitter4j.*;
import twitter4j.auth.AccessToken;

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created with IntelliJ IDEA.
 * User: keisuke
 * Date: 13/06/23
 * Time: 9:45
 * To change this template use File | Settings | File Templates.
 */
public class Main extends JFrame{

    JPanel panel1;
    final JTextField text = null;
    JButton button;

    static InputStream io = null;
    static ArrayList<String> tweet = new ArrayList<String>();
    static long id;
    static List<Status>timeline = null;
    Main(String name){

        setTitle(name); //タイトルの設定
        setSize(500,500); //サイズの設定
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); //×を押した時の動作（プログラム終了）

        /*Panelの設定
         * 背景色の設定→JPanel#setBackgroun
         * サイズの設定→JPanel#setPreferredSize(Dimention)
         * 透明度の設定→JPanel#setOpaque(boolean)不透明でない場合はtrue
         * 枠線の設定　→JPanel#setBorder(Border) interface border
         * */

        JPanel panel1 = new JPanel(); //パネル作成
        final JTextField text = new JTextField();
        JButton button = new JButton("Tweet");

        //Dimension(縦と横に関する情報)の最大値取得
        Dimension d1 = text.getMaximumSize();
        Dimension d2 = button.getMaximumSize();

        //Short型の最大サイズ
        d1.width = Short.MAX_VALUE;
        //ボタンの高さに揃える
        d1.height = d2.height;
        //値をセット
        text.setMaximumSize(d1);

        panel1.setLayout(new BoxLayout(panel1,BoxLayout.LINE_AXIS));
        panel1.add(text);
        panel1.add(button);

        JList<Object> list = new JList<Object>(timeline.toArray());
        list.setCellRenderer(new setTimeline());

        JScrollPane pane = new JScrollPane();
        pane.getViewport().setView(list);
        JPanel panel2 = new JPanel();
        panel2.setLayout(new BoxLayout(panel2,BoxLayout.PAGE_AXIS));
        panel2.add(panel1);
        panel2.add(pane);

        add(panel2);

        button.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String tweet = text.getText();
                Twitter twitter = getInst();
                try {
                    twitter.updateStatus(tweet);
                    text.setText("");
                } catch (TwitterException e1) {
                    e1.printStackTrace();
                }
            }
        });
        setVisible(true); //表示
    }



    static Properties getprop(){
        Properties prop = new Properties();
        try {
            io = new FileInputStream(new File("./src\\twitter4j.properties"));
            prop.load(io);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return prop;
    }

    static Twitter getInst(){
        Twitter twitter = new TwitterFactory().getInstance();
        twitter.setOAuthAccessToken(new AccessToken(getprop().getProperty("oauth.accessToken"),getprop().getProperty("oauth.accessTokenSecret")));
        return twitter;
    }

    class setTimeline implements ListCellRenderer{

        @Override
        public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            Status status = (Status)value;
            //サムネと名前
            JLabel image = null;
            try {
                image = new JLabel(new ImageIcon(new URL(status.getUser().getProfileImageURL())));
            } catch (MalformedURLException e) {
                e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
            }

            String tweet = status.getUser().getName();
            tweet = tweet + "  @" + status.getUser().getScreenName();
            tweet += "\n";
            tweet += status.getText();
            JTextArea name = new JTextArea(tweet);

            String[] array = tweet.split("\n");
            int count = array.length;
            int size = count>2?25*count : 60;

            //パネル
            JPanel panel = new JPanel();
            panel.setLayout(new BoxLayout(panel,BoxLayout.X_AXIS));
            panel.setSize(Short.MAX_VALUE,60);
            panel.setPreferredSize(new Dimension(Short.MAX_VALUE,size));
            panel.setMinimumSize(new Dimension(Short.MAX_VALUE,size));
            panel.setBorder(new LineBorder(Color.black,1));

            image.setSize(50,50);
            image.setPreferredSize(new Dimension(50, 50));
            image.setMinimumSize(new Dimension(50,50));

            Dimension d1 = name.getMaximumSize();
            d1.width = Short.MAX_VALUE;
            name.setMaximumSize(d1);

            name.setRows(count);
            name.setEditable(false);
            name.setPreferredSize(new Dimension(Short.MAX_VALUE,size));
            name.setMargin(new Insets(0,0,5,0));

            panel.add(image);
            panel.add(name);

            return panel;  //To change body of implemented methods use File | Settings | File Templates.
        }
    }

    public static void main(String[] args){
        Properties prop;

        prop = getprop();

        //もしAccessTokenが保存されていなかったら
        if(prop.getProperty("oauth.accessToken")==null){
            new LoginActivity("hoge");
        }

        try {
            io.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        Twitter twitter = getInst();
        try {
            timeline = twitter.getHomeTimeline(new Paging(1,200));
        } catch (TwitterException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }

        for(Status status : timeline){
            tweet.add(status.getText());
        }

        new Main("hoge");

    }
}
