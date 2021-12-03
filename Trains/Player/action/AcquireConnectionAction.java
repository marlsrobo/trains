package action;

import java.util.Objects;
import map.IRailConnection;
import map.RailConnection;
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

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (!(obj instanceof AcquireConnectionAction)) {
            return false;
        }

        AcquireConnectionAction otherRailConnection = (AcquireConnectionAction) obj;
        return this.connection.equals(otherRailConnection.connection);
    }
}
