package design_pattern.behavioral

import scala.collection.mutable

// https://github.com/josephguan/scala-design-patterns/tree/master/behavioral/observer
// the pattern defines a one-to-many dependency between objects:
//           -> observer1
//   subject -> observer2
//           -> observer3
// when the subject changes its state, all its dependents (observers) got notified and update automatically

trait WeatherType

object WeatherType {

  case object SUNNY extends WeatherType
  case object RAINY extends WeatherType
  case object COLD extends WeatherType
}

// observer interface
//   it defines an update interface for observers to be notified of any subject change
trait WeatherObserver {

  def update(currentWeather: WeatherType)
}

// concrete observers
//   it implements the update interface (what to do when the observer got notified)
class Boy extends WeatherObserver {

  override def update(currentWeather: WeatherType): Unit = currentWeather match {
    case WeatherType.SUNNY => println("It is sunny. I can go to play football.")
    case WeatherType.RAINY => println("It is rainy. I can play video games at home.")
    case WeatherType.COLD => println("It is cold. It is a nice for sleeping.")
  }
}

class Girl extends WeatherObserver {

  override def update(currentWeather: WeatherType): Unit = currentWeather match {
    case WeatherType.SUNNY => println("It is sunny. I can go shopping.")
    case WeatherType.RAINY => println("It is rainy. I should take an umbrella and go shopping.")
    case WeatherType.COLD => println("It is cold. I should go to buy some clothes.")
  }
}

// subject (client)
//   it maintains a list of observers that are observing this subject
//   it provides an interface ot attach (subscribe) and detach (unsubscribe) observer objects
class Weather {

  val observers = mutable.ListBuffer[WeatherObserver]() // receivers
  var currentWeather: WeatherType = WeatherType.SUNNY // it stores the subject's state to be observed

  def addObserver(ob: WeatherObserver): Unit = { // subscribe method
    observers.append(ob)
  }

  def removeObserver(ob: WeatherObserver): Unit = { // unsubscribe method
    val index = observers.indexOf(ob)
    if (index != -1) {
      observers.remove(index)
    }
  }

  def changing(weather: WeatherType): Unit = { // allow the client to change the subject's state
    currentWeather = weather
    notifyObservers()
  }

  def notifyObservers(): Unit = { // sends notifications to observers whenever its state changes
    observers.foreach { ob =>
      ob.update(currentWeather)
    }
  }
}

object ObserverApp extends App {
  val weather = new Weather()
  weather.addObserver(new Boy)
  weather.addObserver(new Girl)
  weather.changing(WeatherType.COLD)
  // It is cold. It is a nice for sleeping.
  // It is cold. I should go to buy some clothes.
  weather.changing(WeatherType.RAINY)
  // It is rainy. I can play video games at home.
  // It is rainy. I should take an umbrella and go shopping.
  weather.changing(WeatherType.SUNNY)
  // It is sunny. I can go to play football.
  // It is sunny. I can go shopping.
}
