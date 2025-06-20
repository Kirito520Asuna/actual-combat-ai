package com.actual.combat.basic.core.view;

import com.actual.combat.basic.view.BasicJsonView;

/**
 * @Author yan
 * @Date 2023/7/26 0026 11:46
 * @Description JSON视图
 */
public class BaseJsonView {
    /**
     * 基础
     */
    public interface BaseView extends BasicJsonView.BaseView {
    }

    /**
     * login
     */
    public interface LoginView extends BaseView {
    }

    /**
     * 注册
     */
    public interface RegisterView extends BaseView {
    }

    public interface AddView extends BaseView {
    }

    public interface UpdateView extends BaseView {
    }

    public interface DeleteView extends BaseView {
    }

    public interface WebView extends BaseView {
    }

    public interface AdminView extends BaseView {
    }

    public interface ApplyView extends BaseView {
    }

    public interface ApplyAgreeView extends BaseView {
    }

    public interface ChatView extends BaseView {
    }

    public interface UserView extends BaseView {
    }

    public interface UserChatView extends UserView {
    }

    public interface SendMessageView extends BaseView {
    }

    public interface DtoView extends BaseView {
    }

}
