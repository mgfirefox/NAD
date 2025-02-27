package by.bsuir.nad.client.model;

import by.bsuir.nad.server.db.entity.User;
import lombok.*;

@NoArgsConstructor
@Getter
@Setter
public abstract class UserModel extends SocketModel {
    private User currentUser;
}
