package fi.infinitygrow.gpslocation.data.repository

import fi.infinitygrow.gpslocation.domain.repository.WeatherService



import platform.CoreLocation.CLLocation
import platform.CoreLocation.CLLocationManager
//import platform.CoreLocation.*
import platform.CoreLocation.CLLocationManagerDelegateProtocol
import platform.CoreLocation.kCLLocationAccuracyBest
import platform.UIKit.UIBackgroundTaskIdentifier
import platform.UserNotifications.UNUserNotificationCenter
import platform.darwin.NSObject

//import UIKit
//import UserNotifications
import platform.UIKit.UIApplication
import platform.UIKit.UIResponder

//import Foundation
//import CoreLocation
//import UserNotifications
//import shared // Your KMP shared module

/*
class LocationUpdateManager /*NSObject, CLLocationManagerDelegate*/ :
    CLLocationManagerDelegateProtocol {
    //static let shared = LocationUpdateManager()
    private val locationManager = CLLocationManager()
    private val locationListeners = mutableListOf<(LocationData) -> Unit>()

    init {
        locationManager.delegate = this
        locationManager.allowsBackgroundLocationUpdates = true
        locationManager.pausesLocationUpdatesAutomatically = false
        locationManager.desiredAccuracy = kCLLocationAccuracyBest
        locationManager.showsBackgroundLocationIndicator = true
    }

    fun startUpdates() {
        locationManager.requestAlwaysAuthorization()
        locationManager.startUpdatingLocation()
        println("Location updates started")
    }

    fun stopUpdates() {
        locationManager.stopUpdatingLocation()
        println("Location updates stopped")
    }


    // MARK: - CLLocationManagerDelegate

    fun locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }

        // This will be called periodically in the background
        print("Location update received: \(location)")

        // Perform your background work here
        performBackgroundWork()
    }

    fun locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Location manager failed with error: \(error.localizedDescription)")
    }

    private fun performBackgroundWork() {
        // Your background processing logic here
        print("Performing background work from location update")
    }
}



 */

/*
@UIApplicationMain
class AppDelegate: UIResponder, UIApplicationDelegate {

    fun application(_ application: UIApplication, didFinishLaunchingWithOptions launchOptions: [UIApplication.LaunchOptionsKey: Any]?) -> Bool {
        // Register for push notifications
        PushNotificationManager.shared.registerForPushNotifications()

        // Start location updates
        LocationUpdateManager.shared.startUpdates()

        return true
    }

    // Handle device token registration
    fun application(_ application: UIApplication, didRegisterForRemoteNotificationsWithDeviceToken deviceToken: Data) {
        let tokenParts = deviceToken.map { data in String(format: "%02.2hhx", data) }
        let token = tokenParts.joined()
        print("Device Token: \(token)")

        // Send this token to your server to use for sending push notifications
        sendDeviceTokenToServer(token: token)
    }

    fun application(_ application: UIApplication, didFailToRegisterForRemoteNotificationsWithError error: Error) {
        print("Failed to register for remote notifications: \(error)")
    }

    // Handle silent push notifications
    fun application(_ application: UIApplication, didReceiveRemoteNotification userInfo: [AnyHashable: Any], fetchCompletionHandler completionHandler: @escaping (UIBackgroundFetchResult) -> Void) {

        PushNotificationManager.shared.application(application, didReceiveRemoteNotification: userInfo, fetchCompletionHandler: completionHandler)
    }

    private fun sendDeviceTokenToServer(token: String) {
        // Implement API call to your server with the device token
        // Your server will need this token to send push notifications
    }
}

 */


/*
//@objc class WeatherServiceImpl: NSObject, WeatherService, ObservableObject, CLLocationManagerDelegate {
class WeatherServiceImpl: NSObject, WeatherService {
    private let serviceQueue = DispatchQueue(label: "fi.infinitygrow.weatherservice", qos: .utility)
    private var weatherTask: Task<Void, Never>?
    private var timer: Timer?
    private let weatherRepository: WeatherRepository
    private let locationService: LocationService
    private let textToSpeechHelper: TextToSpeechHelperImpl
    private let favoritesRepository: FavoritesRepositoryImpl
    private let settingsRepository: SettingsRepository

    private var favorites: [ObservationLocation] = []
    private var selectedLocations: [ObservationLocation] = []
    private let selectedLocationsLock = NSLock()

    @Published var isRunning: Bool = false

    private static let NOTIFICATION_ID = 1
    private static let CHANNEL_ID = "weather_channel"

    init(
    weatherRepository: WeatherRepository,
    locationService: LocationService,
    textToSpeechHelper: TextToSpeechHelperImpl,
    favoritesRepository: FavoritesRepositoryImpl,
    settingsRepository: SettingsRepository
    ) {
        self.weatherRepository = weatherRepository
        self.locationService = locationService
        self.textToSpeechHelper = textToSpeechHelper
        self.favoritesRepository = favoritesRepository
        self.settingsRepository = settingsRepository

        super.init()

        // Setup notifications
        requestNotificationPermission()

        // Start observing favorites
        observeFavorites()
    }

    func startWeatherUpdates() {
        weatherTask?.cancel()

        weatherTask = Task {
            do {
                guard !Task.isCancelled else { return }

                // Get TTS settings once
                let ttsSettings = try await settingsRepository.ttsSettingsFlow.first()
                    print("Fetched TTS settings: \(ttsSettings)")

                    while !Task.isCancelled {
                        if !locationService.isPermissionGranted() {
                            notifyNoLocationPermission()
                        } else {
                            await updateWeatherAndNotify(ttsSettings: ttsSettings)
                        }

                        await waitUntilNextObservation()
                    }
                } catch {
                    handleWeatherUpdateError(error: error)
                }
            }

        isRunning = true
    }

    func stopWeatherUpdates() {
        weatherTask?.cancel()
        weatherTask = nil
        textToSpeechHelper.stop()
        isRunning = false
    }

    private func observeFavorites() {
        Task {
            for await favList in favoritesRepository.observeFavorites() {
                await updateSelectedLocations(favList: favList)
            }
        }
    }

    private func updateSelectedLocations(favList: [ObservationLocation]) async {
        selectedLocationsLock.lock()
        defer { selectedLocationsLock.unlock() }

        selectedLocations = favList
    }

    private func updateWeatherAndNotify(ttsSettings: TtsSettings) async {
        guard let location = await locationService.getLocation() else { return }

        let observations = await fetchObservations(location: location)

        if !ttsSettings.includeAllOrClosest {
            let closestObservation = getClosestObservationWithWind(
                    observations: observations,
            userLat: location.latitude,
            userLong: location.longitude
            )
            let favoriteList = getNewestObservationsForSelectedLocations(observations: observations)
            var closestObservationList: [ObservationData] = []

            if let closestObservation = closestObservation {
                closestObservationList.append(closestObservation)
            }
            closestObservationList.append(contentsOf: favoriteList)

            let weatherSpeech = constructWeatherSpeech(
                    observations: closestObservationList,
            location: location,
            ttsSettings: ttsSettings
            )

            if !weatherSpeech.isEmpty {
                textToSpeechHelper.speak(text: weatherSpeech)
            }

            notifyWeatherUpdate(weatherSpeech: weatherSpeech)
        } else {
            let weatherSpeech = constructWeatherSpeech(
                    observations: observations,
            location: location,
            ttsSettings: ttsSettings
            )

            if !weatherSpeech.isEmpty {
                textToSpeechHelper.speak(text: weatherSpeech)
            }

            notifyWeatherUpdate(weatherSpeech: weatherSpeech)
        }
    }

    private func getNewestObservationsForSelectedLocations(observations: [ObservationData]) -> [ObservationData] {
        selectedLocationsLock.lock()
        defer { selectedLocationsLock.unlock() }

        let selectedStationNames = Set(selectedLocations.map { $0.name })
        let filteredObservations = observations.filter { selectedStationNames.contains($0.name) }

        // Group the filtered observations by station name
        var groupedObservations: [String: [ObservationData]] = [:]
        for observation in filteredObservations {
            var obsForStation = groupedObservations[observation.name] ?? []
            obsForStation.append(observation)
            groupedObservations[observation.name] = obsForStation
        }

        // For each group, select observation with latest unixTime
        var result: [ObservationData] = []
        for (_, obsList) in groupedObservations {
            if let latestObs = obsList.max(by: { $0.unixTime < $1.unixTime }) {
            result.append(latestObs)
        }
        }

        return result
    }

    private func fetchObservations(location: KotlinLocation) async -> [ObservationData] {
        selectedLocationsLock.lock()
        defer { selectedLocationsLock.unlock() }

        let observations = try? await weatherRepository.getObservation(
                latitude: location.latitude,
            longitude: location.longitude,
            locations: selectedLocations
            )

            return getNewestObservationsWithWind(observations: observations ?? [])
        }

    private func constructWeatherSpeech(
    observations: [ObservationData],
    location: KotlinLocation,
    ttsSettings: TtsSettings
    ) -> String {
        return observations.map { observation in
                constructLocalizedString(data: observation, location: location, ttsSettings: ttsSettings)
        }.joined(separator: ". ")
    }

    private func notifyWeatherUpdate(weatherSpeech: String) {
        let notificationText = weatherSpeech.isEmpty ?
        NSLocalizedString("weather_updates", comment: "") : weatherSpeech

        sendNotification(
            title: NSLocalizedString("weather_service", comment: ""),
        body: notificationText
        )
    }

    private func notifyNoLocationPermission() {
        sendNotification(
            title: NSLocalizedString("weather_service", comment: ""),
        body: NSLocalizedString("no_location_permission", comment: "")
        )
    }

    private func handleWeatherUpdateError(error: Error) {
        print("WeatherService, Error fetching weather: \(error)")
    }

    private func waitUntilNextObservation() async {
        let now = Date()
        let calendar = Calendar.current
                let minutes = calendar.component(.minute, from: now)

        let nextUpdateMinutes = [2, 12, 22, 32, 42, 52].first(where: { $0 > minutes }) ?? 2
        let minutesToWait = (nextUpdateMinutes - minutes).magnitude < 0 ?
        (nextUpdateMinutes - minutes + 60) : (nextUpdateMinutes - minutes)

        // Sleep for the calculated number of minutes
        try? await Task.sleep(nanoseconds: UInt64(minutesToWait * 60 * 1_000_000_000))
        }

    private func getNewestObservationsWithWind(observations: [ObservationData]) -> [ObservationData] {
        // Group observations by name
        var groupedObservations: [String: [ObservationData]] = [:]
        for observation in observations {
            var obsForStation = groupedObservations[observation.name] ?? []
            obsForStation.append(observation)
            groupedObservations[observation.name] = obsForStation
        }

        // Select the newest observation for each location, filtering out invalid windSpeed
        var result: [ObservationData] = []
        for (_, obsList) in groupedObservations {
            let validObsList = obsList.filter {
                $0.windSpeed.isFinite && $0.windSpeed != 0.0
            }

            if let latestObs = validObsList.max(by: { $0.unixTime < $1.unixTime }) {
            result.append(latestObs)
        }
        }

        return result
    }

    private func getClosestObservationWithWind(
    observations: [ObservationData],
    userLat: Double,
    userLong: Double
    ) -> ObservationData? {
        let validObservations = observations.filter {
            $0.windSpeed.isFinite && $0.windSpeed != 0.0
        }

        return validObservations.min(by: { obs1, obs2 in
            let dist1 = getDistance(
                    longitude1: userLong,
            latitude1: userLat,
            longitude2: obs1.longitude,
            latitude2: obs1.latitude
            )
            let dist2 = getDistance(
                    longitude1: userLong,
            latitude1: userLat,
            longitude2: obs2.longitude,
            latitude2: obs2.latitude
            )
            return dist1 < dist2
        })
    }

    private func getDistance(
    longitude1: Double,
    latitude1: Double,
    longitude2: Double,
    latitude2: Double
    ) -> Double {
        // Haversine formula implementation
        let earthRadius = 6371.0 // km

        let dLat = (latitude2 - latitude1) * .pi / 180
        let dLon = (longitude2 - longitude1) * .pi / 180

        let a = sin(dLat/2) * sin(dLat/2) +
                cos(latitude1 * .pi / 180) * cos(latitude2 * .pi / 180) *
                sin(dLon/2) * sin(dLon/2)

        let c = 2 * atan2(sqrt(a), sqrt(1-a))
        return earthRadius * c
    }

    private func constructLocalizedString(
    data: ObservationData?,
    location: KotlinLocation,
    ttsSettings: TtsSettings
    ) -> String {
        // You'll need to implement the parts constructor similar to the Android version
        let parts = constructLanguageStringNonComposable(
                data: data,
        location: location,
        ttsSettings: ttsSettings
        )

        return parts.map { key, value in
            switch key {
                case "weather_station_name":
                return String(format: NSLocalizedString("weather_station_name", comment: ""), value as! CVarArg)
                case "distance_km":
                return String(format: NSLocalizedString("distance_km", comment: ""), value as! CVarArg)
                case "rain_mm":
                return String(format: NSLocalizedString("rain_mm", comment: ""), value as! CVarArg)
                case "wind_speed":
                return String(format: NSLocalizedString("wind_speed", comment: ""), value as! CVarArg)
                case "wind_gust":
                return String(format: NSLocalizedString("wind_gust", comment: ""), value as! CVarArg)
                case "wind_direction":
                return String(format: NSLocalizedString("wind_direction", comment: ""), value as! CVarArg)
                case "cloud_base":
                return String(format: NSLocalizedString("cloud_base", comment: ""), value as! CVarArg)
                case "fl_65":
                return String(format: NSLocalizedString("fl_65", comment: ""), value as! CVarArg)
                case "fl_95":
                return String(format: NSLocalizedString("fl_95", comment: ""), value as! CVarArg)
                default:
                return value as! String
            }
        }.joined(separator: " ")
    }

    // MARK: - Notification handling

    private func requestNotificationPermission() {
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound, .badge]) { granted, error in
            if granted {
                print("Notification permission granted")
            } else if let error = error {
                print("Notification permission error: \(error)")
            }
        }
    }

    private func sendNotification(title: String, body: String) {
        let content = UNMutableNotificationContent()
        content.title = title
        content.body = body
        content.sound = UNNotificationSound.default

        let request = UNNotificationRequest(
                identifier: UUID().uuidString,
        content: content,
        trigger: nil
        )

        UNUserNotificationCenter.current().add(request)
    }

    // This method would be implemented in your shared code or a helper
    private func constructLanguageStringNonComposable(
    data: ObservationData?,
    location: KotlinLocation,
    ttsSettings: TtsSettings
    ) -> [(String, Any)] {
        // Implement this method based on your shared code logic
        // Return key-value pairs for localized strings
        // This is a placeholder - you'll need to implement the actual logic
        var parts: [(String, Any)] = []

        if let data = data {
            parts.append(("weather_station_name", data.name))
            // Add other parts based on your Android implementation
        }

        return parts
    }
}

// iOS service controller equivalent
class WeatherServiceController: NSObject {
    private static var sharedInstance: WeatherServiceImpl?
    private let serviceFactory: () -> WeatherServiceImpl

    init(serviceFactory: @escaping () -> WeatherServiceImpl) {
        self.serviceFactory = serviceFactory
        super.init()
    }

    func isServiceRunning() -> Bool {
        return WeatherServiceController.sharedInstance?.isRunning ?? false
    }

    func startWeatherService() {
        if WeatherServiceController.sharedInstance == nil {
            WeatherServiceController.sharedInstance = serviceFactory()
        }

        WeatherServiceController.sharedInstance?.startWeatherUpdates()
    }

    func stopWeatherService() {
        WeatherServiceController.sharedInstance?.stopWeatherUpdates()
    }

    func toggleWeatherService() {
        if isServiceRunning() {
            stopWeatherService()
        } else {
            startWeatherService()
        }
    }
}


 */

/*
// MARK: - WeatherService Protocol

protocol WeatherService {
    func startWeatherUpdates()
    func stopWeatherUpdates()
}

// MARK: - WeatherServiceImpl
//
// This class mimics the Android service by using a timer to update weather at specific minute intervals.
// Note that in iOS you may need to register for background fetch or use other background modes (like audio)
// if you need updates while the app is not active.
class WeatherServiceImpl: NSObject, WeatherService {
    // Timer to mimic scheduling tasks (iOS doesn’t have a direct equivalent to Android’s Service)
    private var weatherTimer: Timer? = null

    // Background Task Identifier for iOS background execution
    private var backgroundTask: UIBackgroundTaskIdentifier = .invalid
    // Dependencies – you should replace these with your own implementations.
    private let weatherRepository = WeatherRepository()
    private let locationService = LocationService()
    private let textToSpeechHelper = TextToSpeechHelper()
    private let favoritesRepository = FavoritesRepository()
    private let settingsRepository = SettingsRepository()

    // Keeps track of the selected favorite stations
    private var selectedLocations = [ObservationLocation]()

    override init() {
        super.init()
        // Request notification permission on initialization.
        UNUserNotificationCenter.current().requestAuthorization(options: [.alert, .sound]) { granted, error in
            if let error = error {
                print("Notification permission error: \(error)")
            }
        }
        // observe favorites changes
        favoritesRepository.observeFavorites { [weak self] favs in
            self?.updateSelectedLocations(with: favs)
        }
    }

    // MARK: - WeatherService Protocol Methods

    func startWeatherUpdates() {
        // Start a background task to have extra time.
        backgroundTask = UIApplication.shared.beginBackgroundTask(withName: "WeatherService") {
            // End the task if time expires.
            self.endBackgroundTask()
        }

        // Show a local notification to simulate the running service.
        scheduleNotification(with: NSLocalizedString("Weather Service Running", comment: ""))

        // Use an initial fire date based on our custom schedule
        scheduleNextWeatherUpdate()
    }

    func stopWeatherUpdates() {
        weatherTimer?.invalidate()
        weatherTimer = nil
        textToSpeechHelper.stop()
        endBackgroundTask()
        // Optionally, cancel any displayed notifications if needed.
        UNUserNotificationCenter.current().removeAllDeliveredNotifications()
    }

    // MARK: - Private Methods

    /// Update the selected locations based on the favorite list
    private func updateSelectedLocations(with favorites: [ObservationLocation]) {
        selectedLocations = favorites
    }

    /// Schedule the next weather update based on current time.
    private func scheduleNextWeatherUpdate() {
        let now = Date()
        let calendar = Calendar.current
                let currentMinute = calendar.component(.minute, from: now)
        // The observation minute marks (2, 12, 22, 32, 42, 52)
        var nextUpdateMinute: Int? = [2, 12, 22, 32, 42, 52].first { $0 > currentMinute }

        if nextUpdateMinute == nil {
            // When rolling over to the next hour, use the first mark (2)
            nextUpdateMinute = 2
        }

        var nextUpdateComponents = calendar.dateComponents([.year, .month, .day, .hour], from: now)
        nextUpdateComponents.minute = nextUpdateMinute
        nextUpdateComponents.second = 0

        // If our calculated date is in the past, add one hour.
        var nextUpdateDate = calendar.date(from: nextUpdateComponents) ?? now.addingTimeInterval(60)
        if nextUpdateDate.compare(now) != .orderedDescending {
            nextUpdateDate = calendar.date(byAdding: .hour, value: 1, to: nextUpdateDate)!
        }

        let timeInterval = nextUpdateDate.timeIntervalSince(now)
        print("Scheduling next weather update in \(timeInterval) seconds")

        weatherTimer?.invalidate()
        weatherTimer = Timer.scheduledTimer(withTimeInterval: timeInterval,
            repeats: false) { [weak self] _ in
            self?.performWeatherUpdate()
        }
    }

    /// Performs the weather update operation.
    private func performWeatherUpdate() {
        // Check for location permission.
        guard locationService.isPermissionGranted() else {
            scheduleNotification(with: NSLocalizedString(
                    "No location permission",
                comment: ""
            ))
            scheduleNextWeatherUpdate()
            return
        }

        // Ensure we have a current location.
        guard let location = locationService.getLocation() else {
            scheduleNotification(with: NSLocalizedString(
                    "Location unavailable",
                comment: ""
            ))
            scheduleNextWeatherUpdate()
            return
        }

        // Fetch settings (we assume a synchronous API or use a completion closure accordingly).
        let ttsSettings = settingsRepository.fetchTtsSettings()

        // Fetch observations using the weather repository.
        weatherRepository.getObservations(latitude: location.coordinate.latitude,
        longitude: location.coordinate.longitude,
        favorites: selectedLocations) { [weak self] observations in
            guard let self = self else { return }

            // Determine the weather speech text.
            var weatherSpeech = ""
            if ttsSettings.includeAllOrClosest == false {
                // Find closest observation with wind.
                if let closest = self.getClosestObservationWithWind(
                        observations: observations,
                userLatitude: location.coordinate.latitude,
                userLongitude: location.coordinate.longitude
                ) {
                let favoriteObservations = self.getNewestObservationsForSelectedLocations(
                        observations: observations
                )
                let combinedObservations = [closest] + favoriteObservations
                weatherSpeech = self.constructWeatherSpeech(from: combinedObservations,
                    location: location,
                    settings: ttsSettings)
            }
            } else {
                weatherSpeech = self.constructWeatherSpeech(from: observations,
                    location: location,
                    settings: ttsSettings)
            }

            if !weatherSpeech.isEmpty {
                self.textToSpeechHelper.speak(weatherSpeech)
            }

            // Show a notification with the weather update.
            self.scheduleNotification(with: weatherSpeech.isEmpty ?
            NSLocalizedString("Weather updates", comment: "") : weatherSpeech)

            // Schedule the next update.
            self.scheduleNextWeatherUpdate()
        }
    }

    /// Schedules a local notification with given text.
    private func scheduleNotification(with text: String) {
        let content = UNMutableNotificationContent()
        content.title = NSLocalizedString("Weather Service", comment: "")
        content.body = text
        content.sound = .default

                // Deliver immediately
                let trigger = UNTimeIntervalNotificationTrigger(timeInterval: 1, repeats: false)
        let request = UNNotificationRequest(identifier: "weatherServiceNotification",
        content: content,
        trigger: trigger)

        UNUserNotificationCenter.current().add(request) { error in
                if let error = error {
                    print("Notification scheduling error: \(error)")
                }
        }
    }

    /// End the background task if registered.
    private func endBackgroundTask() {
        if backgroundTask != .invalid {
            UIApplication.shared.endBackgroundTask(backgroundTask)
            backgroundTask = .invalid
        }
    }

    // MARK: - Helpers for Weather Observations

    /// Returns the newest observation for each weather station in selectedLocations.
    private func getNewestObservationsForSelectedLocations(
    observations: [ObservationData]
    ) -> [ObservationData] {
        let selectedStationNames = Set(selectedLocations.map { $0.name })
        let filtered = observations.filter { selectedStationNames.contains($0.name) }
        var result = [String: ObservationData]()
        for obs in filtered {
            if let current = result[obs.name] {
                if obs.unixTime > current.unixTime {
                    result[obs.name] = obs
                }
            } else {
                result[obs.name] = obs
            }
        }
        return Array(result.values)
    }

    /// Returns the closest observation with a valid wind speed.
    private func getClosestObservationWithWind(
    observations: [ObservationData],
    userLatitude: Double,
    userLongitude: Double
    ) -> ObservationData? {
        let validObservations = observations.filter { $0.windSpeed.isFinite && $0.windSpeed != 0.0 }
        return validObservations.min { (a, b) -> Bool in
                let distanceA = getDistance(
                lat1: userLatitude,
            lon1: userLongitude,
            lat2: a.latitude,
            lon2: a.longitude
            )
            let distanceB = getDistance(
                    lat1: userLatitude,
            lon1: userLongitude,
            lat2: b.latitude,
            lon2: b.longitude
            )
            return distanceA < distanceB
        }
    }

    /// Constructs a weather speech string from a list of observations.
    private func constructWeatherSpeech(
    from observations: [ObservationData],
    location: CLLocation,
    settings: TtsSettings
    ) -> String {
        // In this example we join weather strings for the observations.
        // Replace with your own localization and formatting logic.
        let parts = observations.map {
            constructLocalizedString(for: $0, location: location, settings: settings)
        }
        return parts.joined(separator: ". ")
    }

    /// Constructs a localized weather string for an observation.
    private func constructLocalizedString(
    for data: ObservationData,
    location: CLLocation,
    settings: TtsSettings
    ) -> String {
        // In a real implementation, use localization and proper formatting.
        // This is a simple example.
        return "Station \(data.name) at \(data.unixTime) reported wind speed \(data.windSpeed)"
    }

    /// Calculates the approximate distance in meters between two coordinates.
    private func getDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double)
    -> Double {
        let coordinate0 = CLLocation(latitude: lat1, longitude: lon1)
        let coordinate1 = CLLocation(latitude: lat2, longitude: lon2)
        return coordinate0.distance(from


 */


@Suppress("EXPECT_ACTUAL_CLASSIFIERS_ARE_IN_BETA_WARNING")
actual class WeatherServiceImpl : WeatherService {
    override fun startWeatherUpdates() {
        return
    }

    override fun stopWeatherUpdates() {
        return
    }

}

// Expect declaration that will be implemented differently on each platform
actual class WeatherServiceController {
    actual fun isServiceRunning(): Boolean {
       return false
    }

    actual fun startWeatherService() {
        return
    }

    actual fun stopWeatherService() {
        return
    }

    actual fun toggleWeatherService() {
        if (isServiceRunning()) {
            stopWeatherService()
        } else {
            startWeatherService()
        }
    }
}