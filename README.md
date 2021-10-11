
AndroidStories
========  

AndroidStories is a development environment for UI components on Android. It allows you to browse a component library, and interactively develop and test components.

## Getting Started

### Gradle configuration
To start writing stories we need first to add to the example app gradle file:  
kapt plugin to generate the stories provider
```  
plugins {  
	 ...
	 id 'kotlin-kapt'
 }  
```  
and the AndroidStories dependencies
```  
dependencies {  
	 ...
	 // annotations package
	 implementation "com.igenius.androidstories:core:$lastAndroidStoriesVersion"
	 // stories app implementation
	 implementation "com.igenius.androidstories:app:$lastAndroidStoriesVersion"
	 // stories processor
	 kapt "com.igenius.androidstories:processor:$lastAndroidStoriesVersion"
 }  
```  

### App Class and StoriesActivity
To run the stories we need to provide the generated stories to the application,  
to do that we need to extends the `StoriesApp` class in order to instantiate the generated provider

```
class ExampleStoriesApp: StoriesApp() {  
    override val storiesProvider: StoriesProvider  
        get() = AppStoriesProvider()  // generated stories provider
}
```
declare the application class in the manifest and the StoriesActivity
```
<application  
	... 
	android:name="path.to.ExampleStoriesApp"  
	...>
	<activity  
		android:name="com.igenius.androidstories.app.StoriesActivity"  
		android:exported="true">  
		<intent-filter>
			<action android:name="android.intent.action.MAIN" />  
			<category android:name="android.intent.category.LAUNCHER" />  
		</intent-filter>
	</activity>
</application>
```
done!

## How to use

To specify a story we have to annotate with `@Story` a function that returns a `View`
or a `Fragment`.
Function example
```
@Story() // the function name will be the story's title
fun example1(  
    inflater: LayoutInflater,  
  container: ViewGroup?,  
): View = // code that create the view
  
@Story(  
    title = "Example 2 title",  
	description = "Example 2 description"  
)  
fun example2(  
    inflater: LayoutInflater,  
  container: ViewGroup?,  
): View = // code that create the view
```
Fragment story example
```
@Story(  
    title = "Example a total fragment"  
)  
class ExampleFragment: Fragment() {  
    override fun onCreateView(  
        inflater: LayoutInflater,  
  container: ViewGroup?,  
  savedInstanceState: Bundle?  
    ): View = inflater.inflate(R.layout.activity_main, container, false)  
}
```
