package com.acidkitchen

import griffon.core.artifact.GriffonView
import griffon.inject.MVCMember
import griffon.metadata.ArtifactProviderFor
import javax.annotation.Nonnull
import javax.swing.SingleSelectionModel

@ArtifactProviderFor(GriffonView)
class SampleView {
    @MVCMember @Nonnull
    FactoryBuilderSupport builder
    @MVCMember @Nonnull
    SampleModel model
    @MVCMember @Nonnull
    SampleController controller
    void initUI() {

        builder.application(title: application.configuration['application.title'],
            sizeToScene: true, centerOnScreen: true, name: 'mainWindow') {
            scene(fill: BLUE, width: 320, height: 240) {
                gridPane {
                    comboBox(id: 'ipAddressActive',promptText: "ip address", items: model.ipAddressList, row: 0, column: 0,
                            onAction: {
                                model.setCustomIpAddress(it.source.value)
                                controller.checkStatus()
                            },  editable: true, prefWidth: 150)
                    label(id: 'ipStatus', row: 0, column: 1,
                            text: bind(model.statusProperty()))
                    label(id: 'opStatus', row: 8, column: 0,
                            text: bind(model.processStatusProperty()))
                    button(row: 3, column: 0, prefWidth: 100,
                           id: 'clickActionTarget', checkStatusAction)
                    button(row: 3, column: 1, prefWidth: 100,
                            id: 'clickActionTarget', restartAction)
                    button(row: 10, column: 0, prefWidth: 100,
                            id: 'runEceStudioTarget', runEceStudioAction)
                }
            }
        }
    }
}