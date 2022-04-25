package ch.zumbo.jooqdemo;

import org.jooq.Delete;
import org.jooq.ExecuteContext;
import org.jooq.Insert;
import org.jooq.Update;
import org.jooq.conf.ParamType;
import org.jooq.impl.DefaultExecuteListener;

public class AuditListener extends DefaultExecuteListener {
    @Override
    public void start(ExecuteContext ctx) {
        if (ctx.query() instanceof Update ||
                ctx.query() instanceof Insert ||
                ctx.query() instanceof Delete) {
            System.out.println(ctx.query().getSQL(ParamType.INLINED));
        }
    }
}
