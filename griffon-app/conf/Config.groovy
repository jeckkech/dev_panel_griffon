application {
    title = 'dev control panel'
    startupGroups = ['sample']
    autoShutdown = true
}
mvcGroups {
    // MVC Group for "sample"
    'sample' {
        model      = 'com.acidkitchen.SampleModel'
        view       = 'com.acidkitchen.SampleView'
        controller = 'com.acidkitchen.SampleController'
    }
}