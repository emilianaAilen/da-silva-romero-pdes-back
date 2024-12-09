package pdes.unq.com.APC.interfaces.user;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPurchaseResponse extends UserResponse{
   public int cantPurchasesProducts; 

   public UserPurchaseResponse(){}
}
