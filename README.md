# Pokemon

Playing with kotlin and google maps

## Notes

### Solving support libraries versions
_All com.android.support libraries must use the exact same version specification_
https://stackoverflow.com/a/42374426/1329854

### Securing Google Maps Key
[coreflodev@medium](https://medium.com/@coreflodev/how-the-setup-a-google-map-on-an-android-application-the-smart-way-e8e81daea782)

global properties: `user\.gradle/gradle.properties`
```
pokemonMapsApiKey = AIza...
```

`app/build.gradle`
```
    android {
        defaultConfig {
            resValue "string", "google_maps_key", (project.findProperty("pokemonMapsApiKey") ?: "")
        }
    }
```
    
`AndroidManifest.xml`
```xml
<manifest>
    <application >
        <meta-data
            android:name="com.google.android.geo.API_KEY"
            android:value="@string/google_maps_key"/>
    </application>
</manifest>
```


   