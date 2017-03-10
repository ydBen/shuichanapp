package com.jit.shuichan.mina;

import android.content.Context;
import android.util.Log;

import com.jit.shuichan.mina.gsondata.MessageEvent;

import org.apache.mina.core.future.ConnectFuture;
import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;
import org.apache.mina.filter.codec.ProtocolCodecFilter;
import org.apache.mina.filter.codec.textline.LineDelimiter;
import org.apache.mina.filter.codec.textline.TextLineCodecFactory;
import org.apache.mina.filter.logging.LoggingFilter;
import org.apache.mina.transport.socket.nio.NioSocketConnector;
import org.greenrobot.eventbus.EventBus;

import java.net.InetSocketAddress;
import java.nio.charset.Charset;

/**
 * Description:
 * User: chenzheng
 * Date: 2016/12/9 0009
 * Time: 16:21
 */
public class ConnectionManager {

    private static final String BROADCAST_ACTION = "com.commonlibrary.mina.broadcast";
    private static final String MESSAGE = "message";
    private ConnectionConfig mConfig;
    private Context mContext;

    private NioSocketConnector mConnection;
    private IoSession mSession;
    private InetSocketAddress mAddress;

    public ConnectionManager(ConnectionConfig config){

        this.mConfig = config;
        this.mContext = config.getContext();
        init();
    }

    private void init() {
        mAddress = new InetSocketAddress(mConfig.getIp(), mConfig.getPort());
        mConnection = new NioSocketConnector();
        mConnection.getSessionConfig().setReadBufferSize(mConfig.getReadBufferSize());
        mConnection.getFilterChain().addLast("logging", new LoggingFilter());
        mConnection.getFilterChain().addLast("codec", new ProtocolCodecFilter(new TextLineCodecFactory(Charset.forName("UTF-8"),LineDelimiter.WINDOWS.getValue(),LineDelimiter.WINDOWS.getValue())));
        mConnection.setHandler(new DefaultHandler());
        mConnection.setDefaultRemoteAddress(mAddress);
    }

    /**
     * 与服务器连接
     * @return
     */
    public boolean connnect(){
        Log.e("tag", "准备连接");
        try{
            ConnectFuture future = mConnection.connect();
            future.awaitUninterruptibly();
            mSession = future.getSession();

            SessionManager.getInstance().setSeesion(mSession);

        }catch (Exception e){
            e.printStackTrace();
            Log.e("tag", "连接失败");
            return false;
        }

        return mSession == null ? false : true;
    }

    /**
     * 断开连接
     */
    public void disContect(){
        mConnection.dispose();
        mConnection=null;
        mSession=null;
        mAddress=null;
        mContext = null;
        Log.e("tag", "断开连接");
    }

    class DefaultHandler extends IoHandlerAdapter{

       /* private Context mContext;
        private DefaultHandler(Context context){
            this.mContext = context;

        }*/

        @Override
        public void exceptionCaught(IoSession session, Throwable cause) throws Exception {
            Log.d("TEST","客户端发生异常");
            super.exceptionCaught(session, cause);
        }

        @Override
        public void messageSent(IoSession session, Object message) throws Exception {

            super.messageSent(session, message);
            Log.e("tag", "发送给服务器端消息："+message.toString());

        }

        @Override
        public void sessionOpened(IoSession session) throws Exception {
            super.sessionOpened(session);
            Log.e("tag", "sessionOpened：");
            //连接成功
//            EventBus.getDefault().post(new ConnectionEvent(true));

        }

//        @Override
//        public void sessionClosed(IoSession session) throws Exception {
//            super.sessionClosed(session);
//            Log.e("tag","断开网络连接");
//            //告诉对应界面连接断开
//            EventBus.getDefault().post(new ConnectionEvent(false));
//
//        }

        @Override
        public void messageReceived(IoSession session, Object message) throws Exception {
            super.messageReceived(session,message);
            Log.e("tag", "接收到服务器端消息："+message.toString());
/*            if(mContext!=null){
                Intent intent = new Intent(BROADCAST_ACTION);
                intent.putExtra(MESSAGE, message.toString());
                intent.setAction("com.commonlibrary.mina.broadcast");
                LocalBroadcastManager.getInstance(mContext).sendBroadcast(intent);
            }*/

            EventBus.getDefault().post(new MessageEvent(message.toString()));
        }
    }
}
