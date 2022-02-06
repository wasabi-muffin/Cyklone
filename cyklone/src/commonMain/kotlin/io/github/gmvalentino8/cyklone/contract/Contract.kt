package io.github.gmvalentino8.cyklone.contract

interface Contract

/**
 * Models any form of user interaction
 */
interface Intent : Contract

/**
 * Defines logic that should be executed by a processor
 */
interface Action : Contract

/**
 * Result from a processor
 */
interface Result : Contract

/**
 * Describes the state of the view
 */
interface ViewState : Contract
