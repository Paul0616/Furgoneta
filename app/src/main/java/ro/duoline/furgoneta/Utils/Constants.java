package ro.duoline.furgoneta.Utils;

import android.support.v7.widget.CardView;
import android.widget.Spinner;

import java.math.BigDecimal;

import ro.duoline.furgoneta.R;

/**
 * Clasa Constants contine toate adresele pentru request de la server
 *
 */

public class Constants {
    public static final String BASE_URL_STRING = "http://www.duoline.ro/furgoneta";
    public static final String GET_LOGIN = "getlogin.php";
    public static final String GET_USER_STATUS = "getUserStatus.php";
    public static final String GET_LOCATIONS_NUM = "getCountLocationAndProduct.php";
    public static final String GET_ALL_LOCATIONS = "getAllLocations.php";
    public static final String SET_LOCATION = "setLocation.php";
    public static final String DELETE_LOCATION = "setDelLocation.php";
    public static final String GET_ALL_PRODUCTS = "getAllProducts.php";
    public static final String SET_PRODUCT = "setProduct.php";
    public static final String DELETE_PRODUCT = "setDelProduct.php";
    public static final String CHECK_PRODUCT = "setDocCheckProduct.php";
    public static final String GET_ALL_USERS = "getAllUsers.php";
    public static final String DELETE_USER = "setDelUser.php";
    public static final String USER_STATUS = "setUserStatus.php";
    public static final String GET_ROLES = "getRoles.php";
    public static final String SET_USER = "setUser.php";
    public static final String GET_USER_LOCATIONS = "getUserLocations.php";
    public static final String SET_USER_LOCATIONS = "setUserLocations.php";
    public static final String GET_IS_DOC_AVAILABLE = "isDocAvailable.php";
    public static final String GET_TODAY_DOCUMENTS = "getTodayDocById.php";
    public static final String SET_NEW_DOCUMENT = "setNewDoc.php";
    public static final String SET_DEL_DOCUMENT = "setDelDocument.php";
    public static final String GET_LIST_OF_PRODUCTS = "getProductsByDocType.php";
    public static final String SET_LIST_OF_PRODUCTS = "setProductsByDocNo.php";
    public static final String GET_DOCUMENT_PRODUCTS = "getDocumentProducts.php";
    public static final String SET_QUANTITY = "setQuantity.php";
    public static final String SET_DOC_STATUS = "setDocumentStatus.php";
    public static final String GET_FISA_INCHIDERE = "getDayCloseDoc.php";
    public static final String SET_FISA_INCHIDERE = "setFisaInchidere.php";
    public static final String SET_NUMERAR = "setMonetar.php";
    public static final String GET_MONETAR = "getMonetar.php";
    public static final String GET_SOFER_DOCUMENTS = "getDriverDocuments.php";
    public static final String SET_BOUGHT = "setBought.php";
    public static final String GET_ADMIN_VIEW_DOCS = "getAllDocuments.php";
    public static final String GET_AVAILABLE_ENDING = "getAvailableEnding.php";

    public static final String JSON_RESULT = "result";
    public static final String JSON_USER_ID = "userId";
    public static final String JSON_STATUS = "status";
    public static final String JSON_USERNAME = "username";
    public static final String JSON_PASSWORD = "parola";
    public static final String JSON_EMAIL = "email";
    public static final String JSON_ROLE = "rol";
    public static final String JSON_FIRST_NAME = "prenume";
    public static final String JSON_LAST_NAME = "nume";
    public static final String JSON_LOCATIE = "locatie";
    public static final String JSON_ID = "id";
    public static final String JSON_PRODUCT = "produs";
    public static final String JSON_UM = "um";
    public static final String JSON_TYPE = "tip";
    public static final String JSON_SUPPLY = "aprovizionare";
    public static final String JSON_CONSUM = "consum";
    public static final String JSON_FIXTURES = "inventar";
    public static final String JSON_PHONE = "telefon";
    public static final String JSON_NAME = "denumire";
    public static final String JSON_HOUR = "hour";
    public static final String JSON_DAY = "day";
    public static final String JSON_ID_TIP_DOC = "tipDocId";
    public static final String JSON_QUANTITY = "cantitatea";
    public static final String JSON_MOTIV = "motiv";
    public static final String JSON_NUMERAR = "numerar";
    public static final String JSON_CARD = "card";
    public static final String JSON_SOLD_CASA = "soldcasa";
    public static final String JSON_BANI_1 = "bani_1";
    public static final String JSON_BANI_5 = "bani_5";
    public static final String JSON_BANI_10 = "bani_10";
    public static final String JSON_BANI_50 = "bani_50";
    public static final String JSON_LEI_1 = "lei_1";
    public static final String JSON_LEI_5 = "lei_5";
    public static final String JSON_LEI_10 = "lei_10";
    public static final String JSON_LEI_50 = "lei_50";
    public static final String JSON_LEI_100 = "lei_100";
    public static final String JSON_LEI_200 = "lei_200";
    public static final String JSON_LEI_500 = "lei_500";
    public static final String JSON_HEADER = "header";
    public static final String JSON_REASON = "reason";

    public static void setCardEnabled(CardView cardView, boolean enabled){
        int colorEnabled = cardView.getContext().getResources().getColor(R.color.colorAccent);
        int colorDisabled = cardView.getContext().getResources().getColor(R.color.hintcolor);
        cardView.setCardBackgroundColor(enabled ? colorEnabled : colorDisabled);
        cardView.setEnabled(enabled);
    }

    public static int getSpinnerIndex(Spinner spinner, String myString){
        int index = 0;
        for (int i = 0; i<spinner.getCount(); i++){
            if(spinner.getItemAtPosition(i).equals(myString)){
                index = i;
            }
        }
        return index;
    }

    public static float round(float d, int decimalPlace){
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }
}
