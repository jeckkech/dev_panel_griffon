package com.acidkitchen

import griffon.core.artifact.GriffonModel
import griffon.transform.FXObservable
import griffon.metadata.ArtifactProviderFor
import groovyx.javafx.beans.FXBindable
import javafx.collections.FXCollections

@ArtifactProviderFor(GriffonModel)
class SampleModel {
    @FXBindable String customIpAddress;
    @FXObservable List ipAddressList = FXCollections.observableArrayList("172.24.27.140", "10.10.97.236", "10.10.97.235", "10.10.97.229", "172.24.24.144", "172.24.24.145", "172.24.25.144", "172.24.25.144")
    @FXObservable String status = ""
    @FXObservable String processStatus = ""
}