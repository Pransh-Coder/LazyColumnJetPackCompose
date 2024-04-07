package com.chalomobility.lazycolumnjetpackcompose

//https://developer.android.com/training/dependency-injection/hilt-android#groovy -- refer this doc

// ----- Hilt application class --------
// All apps that use Hilt must contain an Application class that is annotated with "@HiltAndroidApp."

// @HiltAndroidApp triggers Hilt's code generation, including a base class for your application
// that serves as the application-level dependency container.

//Eg -
/*@HiltAndroidApp
class ExampleApplication : Application() { ... }*/




//-------- Inject dependencies into Android classes ---------
// Once Hilt is set up in your Application class and an application-level component is available,
// Hilt can provide dependencies to other Android classes that have the @AndroidEntryPoint annotation:

//Eg -
/*@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() { ... }*/

//Hilt currently supports the following Android classes:

/*
-- Application (by using @HiltAndroidApp)
-- ViewModel (by using @HiltViewModel)
-- Activity
-- Fragment
-- View
-- Service
-- BroadcastReceiver
*/

//If you annotate an Android class with @AndroidEntryPoint, then you also must annotate Android
// classes that depend on it. For example, if you annotate a fragment, then you must also annotate
// any activities where you use that fragment.

// @AndroidEntryPoint generates an individual Hilt component for each Android class in your project.
// To obtain dependencies from a component, use the @Inject annotation to perform field injection:

//Eg -

/*@AndroidEntryPoint
class ExampleActivity : AppCompatActivity() {

    @Inject lateinit var analytics: AnalyticsAdapter
    ...
}*/






//------ Define Hilt bindings --------------
//A binding contains the information necessary to provide instances of a type as a dependency.

// One way to provide binding information to Hilt is constructor injection.
// Use the @Inject annotation on the constructor of a class to tell Hilt how to provide instances of that class:

//Eg -

/*
class AnalyticsAdapter @Inject constructor(private val service: AnalyticsService) {
}*/

// The parameters of an annotated constructor of a class are the dependencies of that class.
// In the example, AnalyticsAdapter has AnalyticsService as a dependency.
// Therefore, Hilt must also know how to provide instances of AnalyticsService.




//------------ Hilt modules ---------------------

// Sometimes a type cannot be constructor-injected. This can happen for multiple reasons.
// For example, you cannot constructor-inject an interface.
// In these cases, you can provide Hilt with binding information by using Hilt modules.

// A Hilt module is a class that is annotated with "@Module".
// Like a Dagger module, it informs Hilt how to provide instances of certain types.
//you must annotate Hilt modules with "@InstallIn" to tell Hilt which Android class each module will be used or installed in.





// ------------- Inject interface instances with @Binds --------------------
//If AnalyticsService is an interface, then you cannot constructor-inject it.
// Instead, provide Hilt with the binding information by creating an abstract function annotated with
// @Binds inside a Hilt module.

// ********** IMP **********************************************************************************
// The "@Binds" annotation tells Hilt which implementation to use when it needs to provide an instance of an interface
// The annotated function provides the following information to Hilt:

/*
 -- The function return type tells Hilt what interface the function provides instances of.
 -- The function parameter tells Hilt which implementation to provide.
*/

//Eg -
/*interface AnalyticsService {
    fun analyticsMethods()
}

// Constructor-injected, because Hilt needs to know how to
// provide instances of AnalyticsServiceImpl, too.

class AnalyticsServiceImpl @Inject constructor(...) : AnalyticsService { ... }

@Module
@InstallIn(ActivityComponent::class)
abstract class AnalyticsModule {

    @Binds
    abstract fun bindAnalyticsService(
        analyticsServiceImpl: AnalyticsServiceImpl
    ): AnalyticsService
}*/

// The Hilt module AnalyticsModule is annotated with @InstallIn(ActivityComponent.class) because
// you want Hilt to inject that dependency into ExampleActivity.This annotation means that all of
// the dependencies in AnalyticsModule are available in all of the app's activities.





// ------- Inject instances with @Provides ------------------

// Interfaces are not the only case where you cannot constructor-inject a type.
// Constructor injection is "also not possible" if you don't own the class because it comes from an
// external library (classes like Retrofit, OkHttpClient, or Room databases),
// or if instances must be created with the builder pattern.

// Consider the previous example. If you don't directly own the AnalyticsService class, you can tell
// Hilt how to provide instances of this type by creating a function inside a Hilt module
// and annotating that function with "@Provides".

//*********** IMP **********************************************************************************

// The annotated function supplies the following information to Hilt:

// The function return type tells Hilt what type the function provides instances of.
// The function parameters tell Hilt the dependencies of the corresponding type.
// The function body tells Hilt how to provide an instance of the corresponding type.
// Hilt executes the function body every time it needs to provide an instance of that type.

//Eg -

/*@Module
@InstallIn(ActivityComponent::class)
object AnalyticsModule {

    @Provides
    fun provideAnalyticsService(
        // Potential dependencies of this type
    ): AnalyticsService {
        return Retrofit.Builder()
            .baseUrl("https://example.com")
            .build()
            .create(AnalyticsService::class.java)
    }
}*/




// ---------- Predefined qualifiers in Hilt ----------------
// Hilt provides some predefined qualifiers. For example, as you might need the Context class
// from either the application or the activity, Hilt provides the "@ApplicationContext" and "@ActivityContext" qualifiers.

//Eg -
/*class AnalyticsAdapter @Inject constructor(
    @ActivityContext private val context: Context,
    private val service: AnalyticsService
) { }*/





// ----------------- Generated components for Android classes ------------------

// For each Android class in which you can perform field injection, there's an associated Hilt
// component that you can refer to in the @InstallIn annotation.
// Each Hilt component is responsible for injecting its bindings into the corresponding Android class.

/*
Hilt component                              Injection For

SingletonComponent	                        Application
ActivityRetainedComponent	                N/A
ViewModelComponent	                        ViewModel
ActivityComponent	                        Activity
FragmentComponent	                        Fragment
ViewComponent	                            View
ViewWithFragmentComponent	                View annotated with @WithFragmentBindings
ServiceComponent	                        Service
*/



// -------------- Component lifetimes --------------------------
// Hilt automatically creates and destroys instances of generated component classes following the
// lifecycle of the corresponding Android classes.
/*
Generated component	                    Created at	                           Destroyed at

SingletonComponent	                Application#onCreate()	                Application destroyed
ActivityRetainedComponent	        Activity#onCreate()	                    Activity#onDestroy()
ViewModelComponent	                ViewModel created	                    ViewModel destroyed
ActivityComponent	                Activity#onCreate()	                    Activity#onDestroy()
FragmentComponent	                Fragment#onAttach()	                    Fragment#onDestroy()
ViewComponent	                    View#super()	                        View destroyed
ViewWithFragmentComponent	        View#super()	                        View destroyed
ServiceComponent	                Service#onCreate()	                    Service#onDestroy()

*/