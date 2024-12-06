package ma.ensa.soap.service;


import ma.ensa.soap.beans.Compte;
import ma.ensa.soap.beans.TypeCompte;
import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class SoapService {

    private static final String NAMESPACE = "http://ws.spring.rest.ma/";
    private static final String URL = "http://10.0.2.2:8000/services";
    private static final String METHOD_GET_COMPTES = "getComptes";
    private static final String METHOD_CREATE_COMPTE = "createCompte";
    private static final String METHOD_DELETE_COMPTE = "deleteCompte";
    private static final String SOAP_ACTION = "";

    public List<Compte> getComptes() throws Exception {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_GET_COMPTES);
        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = true;
        envelope.setOutputSoapObject(request);

        HttpTransportSE transport = new HttpTransportSE(URL);
        transport.debug = true;
        transport.call(null, envelope);

        SoapObject response = (SoapObject) envelope.bodyIn;
        List<Compte> comptes = new ArrayList<>();

        for (int i = 0; i < response.getPropertyCount(); i++) {
            SoapObject soapCompte = (SoapObject) response.getProperty(i);
            Compte compte = new Compte(
                    Long.parseLong(soapCompte.getPropertySafelyAsString("id")),
                    Double.parseDouble(soapCompte.getPropertySafelyAsString("solde")),
                    new Date(), // You might want to parse the actual date from the SOAP response
                    TypeCompte.valueOf(soapCompte.getPropertySafelyAsString("type"))
            );
            comptes.add(compte);
        }

        return comptes;
    }

    public boolean createCompte(double solde, TypeCompte type) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_CREATE_COMPTE);
        request.addProperty("solde", String.valueOf(solde));
        request.addProperty("type", type.name());

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        try {
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.debug = true;
            transport.call(SOAP_ACTION, envelope);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteCompte(long id) {
        SoapObject request = new SoapObject(NAMESPACE, METHOD_DELETE_COMPTE);
        request.addProperty("id", String.valueOf(id));

        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);
        envelope.dotNet = false;
        envelope.setOutputSoapObject(request);

        try {
            HttpTransportSE transport = new HttpTransportSE(URL);
            transport.debug = true;
            transport.call(SOAP_ACTION, envelope);
            return Boolean.parseBoolean(envelope.getResponse().toString());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    private Compte mapToCompte(SoapObject soapObject) throws Exception {
        return new Compte(
                Long.parseLong(soapObject.getPropertyAsString("id")),
                Double.parseDouble(soapObject.getPropertyAsString("solde")),
                new SimpleDateFormat("yyyy-MM-dd").parse(soapObject.getPropertyAsString("dateCreation")),
                TypeCompte.valueOf(soapObject.getPropertyAsString("type"))
        );
    }
}
