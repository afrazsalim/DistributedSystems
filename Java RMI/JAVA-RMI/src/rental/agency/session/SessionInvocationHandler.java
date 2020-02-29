package rental.agency.session;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

public class SessionInvocationHandler implements InvocationHandler {

    public SessionInvocationHandler(Session session) {
        this.session = session;
    }

    private Session session;

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        session.activate();
        return method.invoke(session, args);
    }

}
