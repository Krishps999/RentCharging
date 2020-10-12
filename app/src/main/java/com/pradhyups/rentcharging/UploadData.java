package com.pradhyups.rentcharging;

public class UploadData {

    private String mImageName;
    private String mImageUri;

    private int mLocationvalue;
    private String mContactInfoString;
    private String mAddressString;
    private String mNameString;
    private String mStationImage;
    private String mConnectorTypeString;

    public UploadData() {
        //Empty constructor needed for FireBase
    }

    public UploadData(String imageName, String imageUri) {

        if (imageName.trim().equals("")) {
            imageName = "No Name";
        }
            mImageName = imageName;
            mImageUri = imageUri;
    }


    public UploadData(String nameString, String addressString, String contactInfoString, String connectorTypeString, int locationValue, String stationImageUri) {

        if (nameString.trim().equals("")) {
            nameString = "No Name";
        }
        else if (addressString.trim().equals("")) {
            addressString = "No Address";
        }
        else if (contactInfoString.trim().equals("")) {
            contactInfoString = "No Contact Info";
        }
        else if (connectorTypeString.trim().equals("")) {
            connectorTypeString = "No Connector Type Info";
        }

        mNameString = nameString;
        mAddressString = addressString;
        mContactInfoString = contactInfoString;
        mLocationvalue = locationValue;
        mConnectorTypeString = connectorTypeString;
        mStationImage = stationImageUri;
    }

    public String getStationImage() {
        return mStationImage;
    }

    public void setStationImage(String stationImageUri) {
        mStationImage = stationImageUri;
    }

    public String getImageName() {
        return mImageName;
    }
    public String getImageUri() {
        return mImageUri;
    }

    public String getName() {
        return mNameString;
    }

    public String getAddress() {
        return mAddressString;
    }

    public String getContactInfo() {
        return mContactInfoString;
    }

    public int getLocation() {
        return mLocationvalue;
    }

    public String getConnectorType() {
        return mConnectorTypeString;
    }

    public void setImageName(String imageName) {
        mImageName = imageName;
    }

    public void setImageUri(String imageUri) {
        mImageUri = imageUri;
    }

    public void setConnectorType(String connectorTypeString) {
        mConnectorTypeString = connectorTypeString;
    }

    public void setName(String nameString) {
        mNameString = nameString;
    }

    public void setAddress(String addressString) {
        mAddressString = addressString;
    }

    public void setContactInfo(String contactInfoString) {
        mContactInfoString = contactInfoString;
    }

    public void setLocation(int locationValue) {
        mLocationvalue = locationValue;
    }
}
