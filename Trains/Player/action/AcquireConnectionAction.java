package action;

import java.util.Objects;
import map.IRailConnection;
import referee.TrainsReferee.TurnResult;

public class AcquireConnectionAction implements TurnAction {
    private final IRailConnection connection;

    public AcquireConnectionAction(IRailConnection connection) {
        Objects.requireNonNull(connection);

        this.connection = connection;
    }

    @Override
    public <T> T accept(IActionVisitor<T> visitor) {
        return visitor.visitAcquireAction(this);
    }

    public IRailConnection getRailConnection() {
        return this.connection;
    }
}
